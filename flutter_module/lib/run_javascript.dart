import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutterxssmodule/profile.dart';

class MyHomePage extends StatefulWidget {

  final String test;
  MyHomePage({Key key, @required this.test}) : super(key: key);

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String url = "https://b3nac.com";

  final flutterWebviewPlugin = new FlutterWebviewPlugin();
  StreamSubscription<WebViewStateChanged> _onchanged; // here we checked the url state if it loaded or start Load or abort Load

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _onchanged = flutterWebviewPlugin.onStateChanged.listen((WebViewStateChanged state) {
      if (mounted) {
        if(state.type== WebViewState.finishLoad){ // if the full website page loaded
          print("loaded...");
        } else if(state.type== WebViewState.startLoad){ // if the url started loading
          print("start loading...");
        }
      }
    });
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    flutterWebviewPlugin.dispose(); // disposing the webview widget to avoid any leaks
  }

  @override
  Widget build(BuildContext context) {
    return WebviewScaffold(
        url: url,
        withJavascript: true, // run javascript
        withZoom: false, // if you want the user zoom-in and zoom-out
        hidden: true , // put it true if you want to show CircularProgressIndicator while waiting for the page to load

        appBar: AppBar(
          title: Text("Flutter XSS"),
          centerTitle: false,
          elevation: 1, // give the appbar shadows
          iconTheme: IconThemeData(color: Colors.white),
          actions: <Widget>[
            InkWell(
              child: Icon(Icons.refresh),
              onTap: (){
                flutterWebviewPlugin.reload();
              },
            ),

            InkWell(
              child: Icon(Icons.stop),
              onTap: (){
                flutterWebviewPlugin.stopLoading(); // stop loading the url
              },
            ),

            InkWell(
              child: Icon(Icons.remove_red_eye),
              onTap: (){
                flutterWebviewPlugin.show(); // appear the webview widget
              },
            ),

            InkWell(
              child: Icon(Icons.close),
              onTap: (){
                flutterWebviewPlugin.hide(); // hide the webview widget
              },
            ),

            InkWell(
              child: Icon(Icons.arrow_back),
              onTap: (){
                flutterWebviewPlugin.goBack(); // for going back
              },
            ),

            InkWell(
              child: Icon(Icons.forward),
              onTap: (){
                flutterWebviewPlugin.goForward(); // for going forward
              },
            ),

            InkWell(
              child: Icon(Icons.account_circle),
              onTap: (){
              if (widget.test == "onclick=alert(1)") {
                flutterWebviewPlugin.evalJavascript(widget.test);
                storeFlagState() async {
                  SharedPreferences prefs = await SharedPreferences
                      .getInstance();
                  prefs.setString(
                      'flagFourteenButtonColor', "Flag fourteen found!");
                }
                storeFlagState();
                flutterWebviewPlugin.hide();
                Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ProfilePage(),
                    ));
              }
              },
            ),

          ],// make the icons colors inside appbar with white color

        ),
        initialChild: Container( // but if you want to add your own waiting widget just add InitialChild
          color: Colors.white,
          child: const Center(
            child: Text('waiting...'),
          ),)
    );
  }
}