# Project 2 - *MowDigest*

**MowDigest** is an android app that allows a user to search for articles on web using simple filters. The app utilizes [New York Times Search API](http://developer.nytimes.com/docs/read/article_search_api_v2).

Time spent: **X** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **search for news article** by specifying a query and launching a search. Search displays a grid of image results from the New York Times Search API.
* [x] User can click on "settings" which allows selection of **advanced search options** to filter results
* [x] User can configure advanced search filters such as:
  * [x] Begin Date (using a date picker)
  * [x] News desk values (Arts, Fashion & Style, Sports)
  * [x] Sort order (oldest or newest)
* [x] Subsequent searches have any filters applied to the search results
* [x] User can tap on any image in results to see the full text of article **full-screen**
* [x] User can **scroll down to see more articles** by Endless-Scrolling-with-AdapterViews-and-RecyclerView. The maximum number of articles is limited by the API search. 

The following **optional** features are implemented:

* [ ] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [x] Used the **ActionBar SearchView** or custom layout as the query box instead of an EditText
* [ ] User can **share an article link** to their friends or email it to themselves
* [x] Replaced Filter Settings Activity with a lightweight modal overlay by DialogFragment
* [x] Improved the user interface and experiment with image assets and/or styling and coloring

The following **bonus** features are implemented:

* [x] Use the [RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) with the `StaggeredGridLayoutManager` to display improve the grid of image results
* [x] For different news articles that only have text or only have images, use [Heterogenous Layouts](http://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView) with RecyclerView
* [x] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce view boilerplate.
* [ ] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [ ] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [ ] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.com/android/Drawables#vector-drawables) where appropriate.
* [x] Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.
* [ ] Uses [retrolambda expressions](http://guides.codepath.com/android/Lambda-Expressions) to cleanup event handling blocks.
* [x] Leverages the popular [GSON library](http://guides.codepath.com/android/Using-Android-Async-Http-Client#decoding-with-gson-library) to streamline the parsing of JSON data.
* [x] Leverages the [Retrofit networking library](http://guides.codepath.com/android/Consuming-APIs-with-Retrofit) to access the New York Times API.

The following **additional** features are implemented:

* [x] Users can train this app so that it knows what kind of news the users like to see.
  * [x] Users see the most popular news trending right now.
  * [x] Users can swipe right if they like it or swipe left if they don't.
  * [x] Users see endless news.
  * [ ] After certain threshold, users get notified it is about time to digest the news.
* [x] Users can click on digest tab to see digest page, or simply scroll to it.
  * [x] Users can digest news by clicking on them.
  * [x] Users see the news they like (swipe right).
  * [ ] Users see even more news recommended by this app (training only for you).
  * [ ] Users see endless news.
* [x] Users can search news.
  * [x] Users can click on "search" button to start a search.
  * [x] Users see the endless search result depending on the search criteria.
  * [x] Users can click on "filter" button to start a search filter.
  * [x] Users can apply date range.
  * [x] Users can apply sort order.
  * [x] Users can apply category.
  * [ ] Users can click on "reset" button to reset the search filter.
* [x] When users digest a news, they see a detailed news page.
  * [x] Users can scroll to another news.
  * [x] Users see three news.
  * [ ] Users see endless news.
  * [ ] Users can click on "share" button to share a news.
  * [ ] Users can click on "like" button to like or dislike a news.
* [ ] Users can see a statistics page.
  * [ ] Users can click on "reset" button to reset it.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

https://rawgit.com/leoliu1313/Mowdigest/master/Mowdigest.demo.html
<br>

If the above link doesn't work, please click on the following links.

https://youtu.be/EbcAJ2fvkNI
<br>
https://youtu.be/a1IBIMHcNPk
<br>

Or, the following screenshots:

http://imgur.com/a/9AtDo
<br>

<img src='http://i.imgur.com/t9LD8Hm.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='http://i.imgur.com/5we10fZ.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='http://i.imgur.com/t1BfeXe.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='http://i.imgur.com/yUAZ3Qe.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='http://i.imgur.com/ln8Eb2w.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>
<img src='http://i.imgur.com/6TlqeQK.jpg' title='Screenshot' width="300" alt='Screenshot' /><br><br>

Video created with Android Studio and FFmpeg.



## Notes

GSON found there are two types on the same field name.
<br>
http://api.nytimes.com/svc/mostpopular/v2/mostviewed/all-sections/1.json?offset=0&api-key=fb2092b45dc44c299ecf5098b9b1209d
<br>
where media could be
<br>
[] - List<> array. 99% popular news are this type.
<br>
or
<br>
"" - String. Note that only one article is like this. It is really rare and my GSON cannot handle it.
<br>

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android
- [bumptech/glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce boilerplate code.
- [Diolor/Swipecards](https://github.com/Diolor/Swipecards) - SwipeFlingAdapterView is swipeable.
- [google/gson](https://github.com/google/gson) - Gson fromJson() to Java class.
- [square/retrofit](https://github.com/square/retrofit) - HTTP client with one interface, many functions, Call<> enqueue() quries. Based on square/okhttp.
- [facebook/stetho](https://github.com/facebook/stetho) - Debug network activity.
- [borax12/MaterialDateRangePicker](https://github.com/borax12/MaterialDateRangePicker) - Pick date range by modal overlay. Based on wdullaer/MaterialDateTimePicker.
- [afollestad/material-dialogs](https://github.com/afollestad/material-dialogs) - Create custom views for lightweight modal overlay. DialogFragment?
- [qianlvable/ParallaxEffectDemo](https://github.com/qianlvable/ParallaxEffectDemo) - Parallax Effect.
