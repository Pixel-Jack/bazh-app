# Bazh ( Aka Bluesound unofficial)

This is the Android Application to connect with the Bazh-Base so as to connect several Bluetooth speakers together to stream musics.

## Getting Started

You've to install the raspberry first : #Link#

### Prerequisites

- You must have your phone's drivers install on your computer
- The min minSdkVersion is 23

### Installing


Two options :
- download the bazh.apk in your phone (be sure to authorized unsigned APK) and install it.
- Clone this respository, and thanks to a software like AndroidStudio build and launch this application on your phone.

## Running the tests

There is no tests set in this version.


## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* AndroidStudio

## Developpment information :
Protocol set between the bazh-base and the bazh-app :
STATE_CONNECTED = 0;
SPEAKER_DISCONNECTED = 1;
SPEAKER_TO_DISCONNECT = 2;
LIST_SPEAKER = 3;
STOP_LISTE_SPEAKER = 4;
PAIR = 5;
PAIR_FAIL = 6;
PAIR_SUCCESS = 7;
VOLUME = 8;
ERREUR_DECO = 9;

If you want to take a look start from the file src/java/Main/MainActivity. It's the heart of the whole structure.


## Contributing

This projet was a one year project in CentraleSupelec but we had it in mind for a long time.
Now we won't regularly update it but feel free to contribute or ask us anything.


## Authors

Clément Ponthieu
Anneix Alexis
Florent Laure
Carlier-Gubler Benoit

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc

