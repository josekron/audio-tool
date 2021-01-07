# audio-tool #

Manipulate Mp3 files:
- Convert mp3 file to wav.
- Join two mp3 files.
- Overlap two mp3 files.

## How to build a jar ##

```
 ./gradlew build
 ```
 
 ## Usage ##
 
 ```
 AudioToolLocalClient client = new AudioToolFactory().getAudioToolClient(AudioToolFactory.ClientType.LOCAL);

//Convert mp3 file to wav
client.convertMp3ToWav("mp3FileName", "path");

//Join two mp3 files
client.joinMp3("mp3FileName1", "mp3FileName2", "path");

//Overlap two mp3 files
client.blendMp3("mp3FileName1", "mp3FileName2", "path");
 ```
