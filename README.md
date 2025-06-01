# žAIba
ŽAIba is a real-time, AI-driven support system for volunteers who help amphibians during migration. It combines edge machine learning, LoRaWAN communication, and a mobile app that shows real-time frog activity.

# Content
## Software for frog detection
The provided sketch includes the code for connection to The Things Network via LoRaWAN and the processing of live capture audio data through the Colibri IoT’s Nature Guard with classification model. The model for frog species identification will be implemented when a edge IoT device with more process power is available to the team.

## Classification models for the detection of frogs and their species
The first model (_ei-zaiba_final-arduino-1.0.3.zip_) is a classification model, trained on more than 6 hours of mono 2-second audio clips. The data set is divided into 80/20 train/test sets. The model was trained using spectrogram processing block, with input division of data into 1000 ms windows and 500 ms window stride. The classes are “Yes” for a frog present identification and “No” for no frogs present in the captured audio signal.

The second model (_ei-zabe-arduino-1.0.7.zip_) is also a classification model but uses the Mel-filterbank (MFE) energy features to extract a spectrogram from audio signals. Currently successfully identifies four frog species, common in Slovenia:

* _Hyla arborea_ (European tree frog)

* _Bufotes viridis_ (European green toad)

* _Rana temporaria_ (European common frog)

* _Bufo bufo_ (European toad)

## Mobile app žAIba
The mobile app is created for Android 15.0 devices and is currently in the stages of development. 
Additional features such as in-app audio recording with AI-based frog species identification and organization of public campaigns in cooperation with herpetology associations and natural history museums will be added once additional required resources for such functions is available.

## Team
**Pika Križnar**
* Idea development
* Project management
* ML model engineer
* Arduino software development
* Mobile app developer
* Content collection
* UX/UI design
  
**Nina Viktoria Baškarad**
* Arduino software development
* Project coordination
* Demo video animation
* Content creator
  
**Gašper Vrabič**
* LoRaWAN integration
* Arduino software development
* Prototype configuration
* Technical support
* Content creator

**Neža Zore Pokorn**
* ML model preparation
* Content collection
* Sensor enclosure design
* Content creator

## Acknowledgment
The initiative for the project came from **asist. mag. Luka Mali** provided us with the proper equipment and knowledge to bring this idea to life. The project was part of faculty subject Mobilnost in internet stvari, part of Multimedia program at Faculty of Electrical Engineering of University of Ljubljana.
Special thanks to **dr. Tomi Trilar**, museum consultant at the Slovenian Museum of Natural History, for providing authentic frog recordings.
