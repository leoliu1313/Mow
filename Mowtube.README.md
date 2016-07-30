# Project 1 - *Mowtube*

**Mowtube** shows the latest movies currently playing in theaters. The app utilizes the Movie Database API to display images and basic information about these movies to the user.

Time spent: **38** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **scroll through current movies** from the Movie Database API
* [x] Layout is optimized with the [ViewHolder](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern) pattern.
* [x] For each movie displayed, user can see the following details:
  * [x] Title, Poster Image, Overview (Portrait mode)
  * [x] Title, Backdrop Image, Overview (Landscape mode)

The following **optional** features are implemented:

* [x] User can **pull-to-refresh** popular stream to get the latest movies.
* [x] Display a nice default [placeholder graphic](http://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library#configuring-picasso) for each image during loading.
* [x] Improved the user interface through styling and coloring.

The following **bonus** features are implemented:

* [x] Allow user to view details of the movie including ratings and popularity within a separate activity or dialog fragment.
* [x] When viewing a popular movie (a movie voted for more than 5 out of 10) the video should show the full backdrop image as the layout.  Uses [Heterogenous ListViews](http://guides.codepath.com/android/Implementing-a-Heterogenous-ListView) or [Heterogenous RecyclerView](http://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView) to show different layouts.
* [x] Allow video trailers to be played in full-screen using the YouTubePlayerView or YouTubePlayerSupportFragment.
    * [ ] Overlay a play icon for videos that can be played.
    * [x] More popular movies should start a separate activity that plays the video immediately.
    * [x] Less popular videos rely on the detail page should show ratings and a YouTube preview.
* [ ] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [x] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce boilerplate code.
* [x] Apply rounded corners for the poster or background images using [Picasso transformations](https://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library#other-transformations)

The following **additional** features are implemented:

* ★
* [x] Users see a loading screen in the beginning.
* ★
* [x] Users can collapse or extend Toolbar in AppBarLayout by CoordinatorLayout.
    * [x] Default is extended.
    * [x] Scroll down RecyclerView to collapse.
    * [x] Scroll up RecyclerView to extend.
* [x] User can set up Auto-play option by onCreateOptionsMenu, onPrepareOptionsMenu, onOptionsItemSelected.
    * [x] Auto-play on Wi-Fi only.
        * [ ] Detect if Wi-Fi is connected. Not implemented yet. Simply assume Wi-Fi is NOT connected.
    * [x] Auto-play on popular movies. (default)
    * [x] Auto-play always.
    * [ ] Implemented by Shared Preferences.
* [x] Users see tabs like Home, Upcoming, Trending, and Favorite by TabLayout and ViewPager.
    * [ ] Users see icons on tabs instead of texts.
* [x] Users see a ProgressDialog when waiting more than one second for JsonHttpResponseHandler.
* [x] Enhance movie list views.
    * [x] Apply rounded corners for images.
    * [x] Heterogenous-Layouts-inside-RecyclerView.
    * [ ] Users see the average votes at here, not only in detail movie views.
    * [ ] Users can extend or collapse each item for more details.
    * [ ] Users can click something to see a PopupMenu. 
        * [ ] Users can add movies to Favorite.
        * [ ] Users can remove movies from Favorite.
        * [ ] Users can share movies.
    * [ ] Users see endless movie list.
* ★
* [x] Enhance detail movie views.
    * [x] Users can drag detail movie views by DraggableView.
        * [x] Default is maximized.
        * [x] Drag down to minimize the video while keep playing it.
        * [x] When users select different Fragment pages for ViewPager, the minimized video keeps playing.
        * [x] Drag right or up to maximize the video.
        * [x] Drag left to close the video.
    * [x] Users see trending index, release date, overview, category, and production company.
    * [ ] Users can extend or collapse the details.
    * [x] Users see the average vote on RatingBar.
    * [x] Users can vote a score from 1 to 10 on RatingBar.
    * [x] Users see a image slider by SliderLayout.
* ★
* [x] Enhance the behavior of BACK button.
    * [x] Users can press BACK to minimize detail movie views. 
    * [x] Users can press BACK to close fullscreen. 
    * [x] Users see a Toast message when they press BACK in the stream views.
    * [x] Users can press BACK twice to close the app. 
* [x] Enhance the orientation behavior.
    * [x] Users see video keeps playing when orientation configuration changes by onSaveInstanceState, onRestoreInstanceState.
    * [x] Users can press BACK to close video fullscreen. 
    * [x] Users see fullscreen in landscape orientation. 
    * [ ] When movies are playing, Youtube becomes fullscreen if users changes the orientation to landscape. 
* [x] Youtube cannot be initialized. It inconsistently suffers from "SERVICE_VERSION_UPDATE_REQUIRED" or "java.lang.NullPointerException: Attempt to invoke interface method 'boolean com.google.android.youtube.player.YouTubePlayer.isPlaying()' on a null object reference".
    * [x] Automatically restart app. 
    * [ ] Fix the nondeterministic issues.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

https://rawgit.com/leoliu1313/Mowtube/master/demo.html
<br>

If the above link doesn't work, please click on the following links.

https://youtu.be/XrUouAZgQIQ
<br>
https://youtu.be/CZuDY-Xmssw
<br>
https://youtu.be/KazFNKArcDY
<br>

Or, the following screenshots:

<img src='https://raw.githubusercontent.com/leoliu1313/Mowtube/master/week1demo1.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='https://raw.githubusercontent.com/leoliu1313/Mowtube/master/week1demo2.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='https://raw.githubusercontent.com/leoliu1313/Mowtube/master/week1demo3.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>

Video created with Android Studio, FFmpeg, Gifsicle.

## Notes

Describe any challenges encountered while building the app.

Have problems to create PopupMenu for each item of RecyclerView in Fragment. No anchor error.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing.
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android.
- [pedrovgs/DraggablePanel](https://github.com/pedrovgs/DraggablePanel) - DraggableView is draggable.
- [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce boilerplate code.
- [daimajia/AndroidImageSlider](https://github.com/daimajia/AndroidImageSlider) - SliderLayout is an image slider.
