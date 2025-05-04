import os
import sys
import speech_recognition as sr

r = sr.Recognizer()

with sr.Microphone() as source:
    r.adjust_for_ambient_noise(source)
    r.pause_threshold = 2.0
    audio = r.listen(source)

try:
    text = r.recognize_google(audio, language="en-US")
    # Check and remove the prompt "Speak now..." if it exists
    if text.lower().startswith("speak now..."):
        text = text[len("speak now..."):].strip()

    print(text)
except sr.UnknownValueError:
    print("Could not understand.")
except sr.RequestError:
    print("API request failed.")
