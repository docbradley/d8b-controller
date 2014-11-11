# d8b-controller

Client library for interacting with Mackie's d8b console, written in Java.

Instructions for building the interface cable will be posted on http://d8b.adamdbradley.com/

Features:
* Complete control over the user interface components of the d8b ("console").
* Domain model and serializers for d8b console "commands" (instructions sent to the console surface)
* Domain model, parser, and serializers for d8b console "signals" (notifications sent from the console surface)
* Sample apps demonstrating control over and comprehension of the d8b user interface components.
* **COMING SOON:** Control over the audio portions of the d8b ("audio")
* Mackie Control Universal (MCU) protocol implementation
* Domain model, parser, and serializers for MCU "commands" (instructions sent to MCU Master and XT surfaces)
* Domain model, parser, and serializers for MCU "signals" (notifications sent from MCU Master and XT surfaces)
* Sample app that uses three MIDI in/out pairs to presents the d8b as an MCU Master surface and two MCU XT surfaces
