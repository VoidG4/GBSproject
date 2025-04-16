package com.gbs.gbsproject.model;

/**
 * @param sender Can be "user" or "gemini"
 */
public record Message(String sender, String message) {
}
