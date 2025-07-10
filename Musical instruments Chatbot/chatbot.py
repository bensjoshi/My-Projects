import pandas as pd
import nltk
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
import aiml
import numpy as np
import speech_recognition as sr
import pyttsx3
from fuzzywuzzy import fuzz
from fuzzywuzzy import process

# Initialize the AIML Kernel
kern = aiml.Kernel()
kern.setTextEncoding(None)  # To avoid issues with encoding
kern.bootstrap(learnFiles="mybot-basic.xml")  

# Initialize the Text-to-Speech engine
engine = pyttsx3.init()

# Set properties for voice
engine.setProperty('rate', 150)  # Speed of speech
engine.setProperty('volume', 1)  # Volume level (0.0 to 1.0)

# Ensure necessary nltk data files are available
nltk.download('punkt')

# Load the external Q/A knowledge base (subject, predicate, object)
qa_data = pd.read_csv('qa_brass_instruments.csv')
kb_data = pd.read_csv('kb_brass_instruments.csv')

# Clean column names in case there are extra spaces
qa_data.columns = qa_data.columns.str.strip()
kb_data.columns = kb_data.columns.str.strip()

# Load the trained Keras model
model = load_model('best_model.keras')  

# Create a TF-IDF Vectorizer for the Q/A matching system
vectorizer = TfidfVectorizer(stop_words='english')

# Extract questions and answers from the Q/A knowledge base
questions = qa_data['question'].tolist()
answers = qa_data['answer'].tolist()

# Transform the questions into TF-IDF vectors
tfidf_matrix = vectorizer.fit_transform(questions)

# Store facts in a dictionary for easier manipulation (First-Order Logic)
kb_facts = {}

for index, row in kb_data.iterrows():
    subject = row['subject'].strip().lower()
    predicate = row['predicate'].strip().lower()
    obj = row['obj'].strip().lower()
    fact_key = (subject, predicate)

    # Store the fact in correct format
    if fact_key not in kb_facts:
        kb_facts[fact_key] = obj

# Function to find the most similar question using Cosine Similarity
def find_most_similar_question(user_query):
    user_query_tfidf = vectorizer.transform([user_query])
    cosine_similarities = cosine_similarity(user_query_tfidf, tfidf_matrix)
    most_similar_idx = cosine_similarities.argmax()
    return questions[most_similar_idx], answers[most_similar_idx]

# Function to check for contradictions in the knowledge base (First-Order Logic)
def check_for_contradiction(subject, predicate, obj):

    # Fuzzy matching the subject, predicate, and object to handle slight variations
    fact_key = (subject.lower().strip(), predicate.lower().strip())
    
    if fact_key in kb_facts:
        stored_obj = kb_facts[fact_key]
        # Apply fuzzy matching between the stored object and the new one
        if fuzz.partial_ratio(stored_obj, obj.lower().strip()) < 80:  
            return True  # There is a contradiction
    # Explicit contradiction for known facts
    known_contradictions = [
        ("trumpet", "is", "not a brass instrument"),
        ("trombone", "is", "not a brass instrument"),
        ("saxophone", "is", "a brass instrument")
    ]
    
    if (subject.lower().strip(), predicate.lower().strip(), obj.lower().strip()) in known_contradictions:
        return True  
    
    return False


def resolve_statement(statement):
    # List of predicates we want to handle
    predicates = [" is ", " has ", " plays ", " is played by ", " has a ", " is a "]
    
    # Try to split the statement based on available predicates
    for predicate in predicates:
        if predicate in statement:
            parts = statement.split(predicate)
            subject = parts[0].strip()
            obj = parts[1].strip()

            # Check for contradictions
            if check_for_contradiction(subject, predicate.strip(), obj):
                return "Chatbot: That is not correct."
            else:
                # Return "correct" along with the fact
                return f"Chatbot: Correct. {subject} {predicate.strip()} {obj}."
    
    return "Chatbot: Sorry, I couldn't understand the statement. Please use a valid format."


def listen_to_voice_command():
    recognizer = sr.Recognizer()
    with sr.Microphone() as source:
        print("\nListening for your command...")
        recognizer.adjust_for_ambient_noise(source)
        audio = recognizer.listen(source)
        try:
            # Convert speech to text
            command = recognizer.recognize_google(audio)
            print(f"\nYou said: {command}")
            return command.lower()
        except sr.UnknownValueError:
            print("\nSorry, I could not understand that.")
            return None
        except sr.RequestError as e:
            print(f"\nCould not request results from Google Speech Recognition service; {e}")
            return None

def speak_text(text):
    print(f"\n{text}")  # Print the response to the console for debugging
    engine.say(text)
    engine.runAndWait()

def respond_to_query(user_query):
    response_triggered = False  # Flag to track if any response logic has been triggered

    # Check if the user query is an image (brass or woodwind)
    if user_query.lower().endswith(('.jpg', '.jpeg', '.png')):  
        try:
            # Preprocess the image to match the model input
            img_path = user_query
            img = image.load_img(img_path, target_size=(28, 28), color_mode='rgb')  
            img_array = image.img_to_array(img)  # Convert the image to an array
            img_array = np.expand_dims(img_array, axis=0)  # Add batch dimension
            img_array = img_array / 255.0  # Normalize the image

            # Predict using the trained model
            prediction = model.predict(img_array)
            predicted_class = "brass" if prediction < 0.5 else "woodwind"

            print(f"\nChatbot: The instrument in the image is predicted to be: {predicted_class}\n")
            response_triggered = True
        except Exception as e:
            print(f"\nError processing the image: {e}\n")

    # If the query is not an image, proceed to AIML and logical responses
    if not response_triggered:
     
        predefined_inputs = [
            "hello", "hi", "how are you", "bye", "exit", "thank you", "thanks"
        ]

        # Check if the user query matches a predefined input for AIML response
        if any(keyword in user_query.lower() for keyword in predefined_inputs):
            aiml_response = kern.respond(user_query)
            if aiml_response.strip():
                print(f"\nChatbot: {aiml_response}")
            else:
                print(f"\nChatbot: Sorry, I don't know how to respond to that.\n")
            response_triggered = True

        # Otherwise, handle logical reasoning or question-answer matching
        elif not response_triggered:

            if "i know that" in user_query.lower():
                parts = user_query.lower().replace("i know that", "").strip()

                # Define a list of predicates  to recognize
                predicates = [" is ", " has ", " plays ", " is played by ", " has a ", " is a "]

                # Try to split based on the predicates
                for predicate in predicates:
                    if predicate in parts:
                        subject, remainder = parts.split(predicate)
                        subject = subject.strip()
                        obj = remainder.strip()

                        # Check for contradictions before adding to the KB
                        if check_for_contradiction(subject, predicate.strip(), obj):
                            print(f"\nChatbot: Sorry, this contradicts what I know.\n")
                        else:
                            # Append new fact to the KB CSV file in the correct format
                            new_fact = pd.DataFrame({'subject': [subject], 'predicate': [predicate.strip()], 'obj': [obj]})
                            
                            # Append without the header (so that it doesn't overwrite the file)
                            new_fact.to_csv('kb_brass_instruments.csv', mode='a', header=False, index=False)
                             # Also add it to the in-memory KB (kb_facts) in the same format
                            kb_facts[(subject.lower().strip(), predicate.strip())] = obj.lower().strip()
                            print(f"\nChatbot: OK, I will remember that {subject} {predicate.strip()} {obj}.\n") 
                            response_triggered = True
                        break
                else:
                    print("\nChatbot: Sorry, I couldn't parse that correctly. Make sure to use the format 'I know that <subject> <predicate> <object>'.\n")

            # Handle "Check that ... is ..." and other predicates
            elif "check that" in user_query.lower():
                parts = user_query.lower().replace("check that", "").strip()

                # Define a list of predicates to check
                predicates = [" is ", " has ", " plays ", " is played by ", " has a ", " is a "]

                for predicate in predicates:
                    if predicate in parts:
                        subject, remainder = parts.split(predicate)
                        subject = subject.strip()
                        obj = remainder.strip()

                        # Perform logical resolution
                        result = resolve_statement(f"{subject} {predicate.strip()} {obj}")
                        print(f"\n{result}")
                        response_triggered = True
                        break
                else:
                    print("\nChatbot: Sorry, I couldn't parse that correctly. Make sure to use the format 'Check that <subject> <predicate> <object>'.\n")

            # Handle question-answer matching if the query does not match the logical pattern
            else:
                most_similar_question, answer = find_most_similar_question(user_query)
                print(f"\nChatbot: {answer}")
                response_triggered = True

print("\nChatbot: Hello! I am a chatbot. \nChatbot: Type 'voice' to speak to me or type a query (type 'exit' to quit):\n")

while True:
    # User chooses to use voice command or text input
    user_input = input("\nUser: ")

    if user_input.lower() == 'voice':
        print("\nChatbot: Voice command activated. Speak now.")
        speak_text("Ask me a question")
        user_input = listen_to_voice_command()
        if user_input:
            respond_to_query(user_input)
    elif user_input.lower() == 'exit':
        print("Chatbot: Goodbye!\n")
        speak_text("Goodbye")
        break
    else:
        respond_to_query(user_input)
