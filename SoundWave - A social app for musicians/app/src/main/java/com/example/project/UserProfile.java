package com.example.project;

import java.util.List;

public class UserProfile {
    private String username;
    private List<String> instruments;
    private String location;
    private String bio;
    private List<String> genres;
    private List<String> socialLinks;
    private int followersCount;
    private int followingCount;
    private List<String> followers;
    private List<String> following;
    private String profileImagePath;

    public UserProfile() {
        // Firebase needs this constructor for deserialization
    }

    public UserProfile(
            String username,
            List<String> instruments,
            String location,
            String bio,
            List<String> genres,
            List<String> socialLinks,
            int followersCount,
            int followingCount,
            List<String> followers,
            List<String> following,
            String profileImagePath) {
        this.username = username;
        this.instruments = instruments;
        this.location = location;
        this.bio = bio;
        this.genres = genres;
        this.socialLinks = socialLinks;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.followers = followers;
        this.following = following;
        this.profileImagePath = profileImagePath;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public List<String> getInstruments() {
        return instruments;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getSocialLinks() {
        return socialLinks;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setInstruments(List<String> instruments) {
        this.instruments = instruments;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setSocialLinks(List<String> socialLinks) {
        this.socialLinks = socialLinks;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
