import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import keras_tuner as kt  # Import Keras Tuner

# Set parameters
output_classes = 1  # Only 1 output neuron for binary classification (brass vs woodwind)
image_size = (28, 28)  # Resize images to 28x28

# Set the path to the train and test directories
train_dir = r'C:\Users\bensj\OneDrive - Nottingham Trent University\AI coursework\train'  # Path to the train directory
test_dir = r'C:\Users\bensj\OneDrive - Nottingham Trent University\AI coursework\test'    # Path to the test directory

# Load the local dataset using ImageDataGenerator
train_datagen = ImageDataGenerator(rescale=1./255)  # Rescale pixel values to [0, 1]
test_datagen = ImageDataGenerator(rescale=1./255)   # Rescale pixel values to [0, 1]

# Load training and testing data using flow_from_directory
train_generator = train_datagen.flow_from_directory(
    train_dir,  # Path to the training directory
    target_size=image_size,  # Resize images to the specified size
    batch_size=128,  # Number of images to process in each batch
    color_mode='rgb',  # Use 'rgb' for colored images or 'grayscale' for grayscale images
    class_mode='binary',  # Binary classification: brass or woodwind
)

test_generator = test_datagen.flow_from_directory(
    test_dir,  # Path to the test directory
    target_size=image_size,  # Resize images to the specified size
    batch_size=128,  # Number of images to process in each batch
    color_mode='rgb',  # Use 'rgb' for colored images or 'grayscale' for grayscale images
    class_mode='binary',  # Binary classification: brass or woodwind
)

# Define the model-building function
def build_model(hp):
    model = keras.Sequential([
        keras.Input(shape=(28, 28, 3)),  # Input shape for RGB images (3 channels)
        keras.layers.Conv2D(
            filters=hp.Int('filters_1', min_value=32, max_value=128, step=32), 
            kernel_size=(3, 3), 
            activation="relu"
        ),
        keras.layers.MaxPooling2D(pool_size=(2, 2)),
        keras.layers.Conv2D(
            filters=hp.Int('filters_2', min_value=64, max_value=256, step=64), 
            kernel_size=(3, 3), 
            activation="relu"
        ),
        keras.layers.MaxPooling2D(pool_size=(2, 2)),
        keras.layers.Flatten(),
        keras.layers.Dropout(hp.Float('dropout_rate', min_value=0.3, max_value=0.7, step=0.1)),
        keras.layers.Dense(
            units=hp.Int('units', min_value=128, max_value=512, step=128),
            activation="relu"
        ),
        keras.layers.Dense(output_classes, activation="sigmoid"),
    ])
    
    model.compile(
        optimizer=keras.optimizers.Adam(
            learning_rate=hp.Float('learning_rate', min_value=1e-5, max_value=1e-3, sampling='LOG')
        ),
        loss=keras.losses.BinaryCrossentropy(),
        metrics=['accuracy']
    )
    
    return model

# Initialize the Keras Tuner
tuner = kt.Hyperband(
    build_model,
    objective='val_accuracy',  # Optimize for validation accuracy
    max_epochs=5,  # Set the maximum number of epochs per trial
    factor=3,  # The scaling factor to decide the number of trials
    directory='training_results',  # Directory to store results
    project_name='brass_woodwind_model_tuning'
)

# Perform hyperparameter search with a total of 10 trials (approx.)
tuner.search(train_generator, epochs=10, validation_data=test_generator)

# Retrieve the best model from the tuner
best_model = tuner.get_best_models(num_models=1)[0]

# Print the summary of the best model
best_model.summary()

# Train the best model on the full dataset
best_model.fit(train_generator, epochs=10)

# Evaluate the best model on the test dataset
test_loss, test_acc = best_model.evaluate(test_generator)
print("\nTest accuracy:", test_acc)
