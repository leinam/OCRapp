# OCRapp
Text Recognition App using Google ML Kit

Making use of the device camera app passing the image through intent. (declare permissions in manifest as well as the firebase dependencies in the gradle.)

Converting to Bitmap object (no saving to storage)
Then processing Bitmap using Firebase Vision library 

The photographs are broken into text blocks which can be loaded into a List 
Iterate through the list to get the individual text detected.

Reference made to Android Developer documentation and CodeLabs.

