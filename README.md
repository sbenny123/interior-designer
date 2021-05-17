# Horizon Interior Design Application
Horizon Interior Design is an android application for users wanting to redecorate their rooms or those just trying out different styles. Using augmented reality and the mobile’s camera, users can visualise and experiment with different colours and furniture items within their own room. 

The application uses Sceneform to configure and detect areas where items could be placed. It allows users to move, rotate and edit 3D furniture models they have selected from the menu. Once they are happy with their design, users can save their creations and try out designs with other items.

**Project supervisor:** Thomas Wennekers

# Requirements
  - OpenGL 3.4 +
  - SDK API level 24 + / Android 7.0 +
  - Device must be compatible with ARCore. Please check [here](https://developers.google.com/ar/devices).
  
# Third-party libraries used:
- [koral--/-android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [Sceneform v1.16.0](https://github.com/ThomasGorisse/sceneform-android-sdk) 


# Third-part assets:
3D Models credited to:
•	Danny Bitman (Divan)
•	Momo iStaging (Sofas, recliner chair, bench sofa)
•	Rodrigo Moya (Brown sofa)
•	Ryan Donaldson (Office chair)

Icons:
Made by Freepik from www.flaticon.com


# User Guide
# Item Selection
When first loaded, the user is shown an item selection screen with the available items they can view in augmented reality.

Browse through the categories by swiping left and right or by pressing the tab of the category you wish to view.


# Ar view
Once an item has been selected, the AR camera page will be shown. 

In this view, moving the phone around the floor will help it to detect the surface that the item can be placed in.

When a plane is detected that the item can be placed in, a series of dots will appear. Tap the area to place the item.

The camera button takes a photo which will be saved to a folder called HorizonInterior. The photos are usually picked up in the phone’s gallery as well.

The sofa icon goes to the item selection page again where you can select another item to add to the AR view.

When an item is moved or selected, a button appears on the left which will include options to change the design of the model or delete it.

