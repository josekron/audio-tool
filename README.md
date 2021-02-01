# audio-tool #

Manipulate audio files:
- Convert audio file to wav.
- Join two audio files.
- Overlap two audio files.
- Convert an InputStream (mp3) to mp3 file.
- Get the duration in seconds of an audio file.

## How to build a jar ##

```
 ./gradlew build
 ```
 
 ## Usage ##
 
 ```
 AudioToolLocalClient client = AudioToolClient.getInstance();

//Convert mp3 file to wav
client.convertMp3ToWav("mp3FileName", "path");

//Join two audio files (wav or mp3)
client.joinAudio("mp3FileName1", "mp3FileName2", AudioToolClient.AudioType, "path");

//Overlap two audio files (wav or mp3)
client.blendAudio("mp3FileName1", "mp3FileName2", AudioToolClient.AudioType, "path");

//Return the duration in seconds of an audio file (wav or mp3)
client.durationInSeconds("mp3FileName1", "mp3FileName2", AudioToolClient.AudioType, "path");
 ```
