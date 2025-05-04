from gtts import gTTS
import os
import sys

# Get text from command-line argument
text = sys.argv[1]

# Create TTS object with British English
tts = gTTS(text=text, lang='en', tld='co.uk')

# Save the speech as an MP3 file
tts.save("speech.mp3")