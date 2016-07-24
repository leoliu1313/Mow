# Project 1 - *Mowtube*

**Mowtube** shows the latest movies currently playing in theaters. The app utilizes the Movie Database API to display images and basic information about these movies to the user.

Time spent: **X** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **scroll through current movies** from the Movie Database API
* [x] Layout is optimized with the [ViewHolder](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern) pattern.
* [x] For each movie displayed, user can see the following details:
  * [x] Title, Poster Image, Overview (Portrait mode)
  * [x] Title, Backdrop Image, Overview (Landscape mode)

The following **optional** features are implemented:

* [ ] User can **pull-to-refresh** popular stream to get the latest movies.
* [x] Display a nice default [placeholder graphic](http://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library#configuring-picasso) for each image during loading.
* [x] Improved the user interface through styling and coloring.

The following **bonus** features are implemented:

* [x] Allow user to view details of the movie including ratings and popularity within a separate activity or dialog fragment.
* [ ] When viewing a popular movie (i.e. a movie voted for more than 5) the video should show the full backdrop image as the layout.  Uses [Heterogenous ListViews](http://guides.codepath.com/android/Implementing-a-Heterogenous-ListView) or [Heterogenous RecyclerView](http://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView) to show different layouts.
* [ ] Allow video trailers to be played in full-screen using the YouTubePlayerView.
    * [ ] Overlay a play icon for videos that can be played.
    * [ ] More popular movies should start a separate activity that plays the video immediately.
    * [ ] Less popular videos rely on the detail page should show ratings and a YouTube preview.
* [ ] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [x] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce boilerplate code.
* [x] Apply rounded corners for the poster or background images using [Picasso transformations](https://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library#other-transformations)

The following **additional** features are implemented:

* [ ] Show loading screen in the beginning.
* [x] Use CoordinatorLayout to collapse/extend Toolbar in AppBarLayout.
    * [x] Default is extended.
    * [x] Scroll down RecyclerView to collapse.
    * [x] Scroll up RecyclerView to extend.
* [x] Setting supports Auto-play on Wi-Fi only or always
    * [ ] Detect if Wi-Fi is connected. (When this is still not implemented, simply assume Wi-Fi is not connected.)
* [x] Use TabLayout and ViewPager to show Home, Upcoming, Trending, and Favorite.
* [x] Users see a ProgressDialog when waiting more than one second for JsonHttpResponseHandler.
* [x] Use com.github.pedrovgs:draggablepanel, DraggableView to play videos.
    * [x] Default is maximized.
    * [x] Drag down to minimize the video while keep playing it.
    * [x] When users select different Fragment pages for ViewPager, the minimized video keeps playing.
    * [x] Drag right or up to maximize the video.
    * [x] Drag left to close the video.
* [x] Enhance detail movie views.
    * [x] Users see trending index, release date, overview, category, and production company.
    * [x] Users see the average vote on RatingBar.
    * [x] Users can vote a score from 1 to 10 on RatingBar.
    * [x] Users see a image slider by SliderLayout. 
* [x] Restart app when youtube api cannot be initialized. It inconsistently suffers from SERVICE_VERSION_UPDATE_REQUIRED.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/link/to/your/gif/file.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright [Ching-yao Liu] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.