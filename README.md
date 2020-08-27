# Digit Recognition

This project is devoted to a classic ML problem - digit recognition.  
Two models were trained on the MNIST dataset with the help of the Keras framework and deployed in a simple android app to test their live preformance. The difference between 
the models lies in the training data. The first model used the data from MNIST in its initial form - centered and normalized by size. The second model employed a different approach: the data
was augmented - rotated and zoomed in order to simulate the inconsistency of user input.

## Demo

![Demo Gif 1](https://github.com/IDSviatoslav/NumberRecognition/blob/master/demo/normalModel.gif)  
*Note*: the first model, user input is accurate
  
    
      
![Demo Gif 1](https://github.com/IDSviatoslav/NumberRecognition/blob/master/demo/augModel.gif)  
*Note*: first vs second model in case of sloppy user input

