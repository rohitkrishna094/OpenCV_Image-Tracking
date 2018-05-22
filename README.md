# OpenCV_Image-Tracking

This is a java project for my CS 586: Image Processing Course. Its based on the tutorial from [Kyle Hounslow](https://www.youtube.com/channel/UCJ2b0kP6Hwc_R8ebv8P2f9w). Also special thanks to [doppelgunner]https://www.youtube.com/channel/UCjd_DY1LawVuZuLteDbVabQ as I used his snake game for a small demo of my image tracking application which is explained below.

Note: Download the library files from here: [Libraries](https://www.dropbox.com/s/bvbbwsuwltgzz9k/libraries.zip?dl=0).

### Running it on your machine: (only tested with windows, doesn't work for other operating systems as of now)
1. Download the project using git clone. 
2. Import src folder into your IDE (eclipse in my case).
3. Now add slick.jar, slick-util.jar, lwjgl.jar, lwjgl_util.jar, opencv-320.jar to your build path.
4. Also make sure to add natives(only for windows in the link provided above) for both opencv and lwjgl.
5. Now build and run your application provided you have a webcam.

### Demo
1. Once you run the application you will get a SWING based GUI as shown in the below GIF.
2. You can flip the camera if you want by clicking on flip button.
3. Click threshold button and adjust the sliders on the right until your target object is the only one that's in white while the rest of the background is in black as shown in GIF below.
4. Now click morph. You can now change the values of erosion and dilation by using the corresponding SWING spinners on the right.
5. Now click on track button. You will now notice a green colored circle tracking your target object(in my demo gif it's a ping pong ball :P). 
6. You can change the tracking color from green to any color using RGB SWING spinners on the right.
   ![1](https://user-images.githubusercontent.com/18495886/40338896-5866fa1c-5d3d-11e8-8a77-6188bda8f4ff.gif)
7. As a final surprise, you can now click the start button which will launch another window which is a snake game. You can use the tracking window as a controller to control the snake in another window. Click grid button to get the tracking grid. The tracking grid is only programmed to be a plus. So only 1, 3, 4, 5, 7 indices of this grid can be used to control your snake. Also after every move, you have to come back to the center of the grid(which is indexed at 4 or red color as shown below). It's almost similar to a manual shit gear system.

   ![3](https://user-images.githubusercontent.com/18495886/40338837-0c1d4c92-5d3d-11e8-9803-a59805f9b414.png)
8. The demo for this snake game is as shown in the below gif.

   ![2](https://user-images.githubusercontent.com/18495886/40338876-3e37d6de-5d3d-11e8-82e9-c8091e96d463.gif)
   
### Known Bugs
* The program crashes if you click output button before clicking the track button.

### Future Implementations: Todo
* To automate the adjusting of sliders.
