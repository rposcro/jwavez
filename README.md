[JWaveZ Project Wiki](https://github.com/rposcro/jwavez/wiki)

# Java ZWave Package
I started this projects for couple of different reasons where ZWave technology exploration was the main factor. I simply consider writing code and investigation of results as the most efficient learnig curve. It's not really clear what direction this project takes in the coming months, but for sure I will keep similar modules organisation like it is today.
Current and further development directions will be driven by my personal needs in the field of automation of personal areas. Certainly, if you find the project promising enough, you're more than welcome to bring your own needs, ideas and solutions.

# Modules
## jwavez-core
This module is core due to its business meaning. It defines core ZWave device and command classes, provides means for command payload construction, recognition and parsing.

## jwavez-serial
This is module for serial communication with USB dongles. Still very experimental, very first attempt to cover the area, many problems have arisen when writing and using this code. It is planned to rewrite it completely as a new module, however the intention is not to replace it completely. The module like is shaped today opens really nice opportunities for serial zwave communication details. Some major weaknesses:
* Too complex, hard to understand and maintain
* Poor inbound/outbound frame collision support
* No support for requests retransmission, communication cancellation, etc
* Inbound frames handling is too redundant, unnecessary memory and cpu consumption
* Considered to be fragile to any communication issues

## jwavez-net-tool
Command line tool to manage ZWave network nodes. Separate documentation with examples can be found here: [JWaveZ CLI Network Tool](https://github.com/rposcro/jwavez/wiki/JWaveZ-CLI-Network-Tool)

## jwavez-examples
Just snippets of code to show, test, and check how the code really works. It's doubtful to actively continue development here as the network tool module is taking precendence over it. Possibly to be removed in the future when it becomes too absorbing to keep it up to dated.
