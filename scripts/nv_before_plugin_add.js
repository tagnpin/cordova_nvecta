#!/usr/bin/env node

var fs = require("fs");
var path = require("path");
var rootdir = process.env.PWD;

/* register application class in manifest file */
var APPLICATION_CLASS = "com.plugin.notifyvisitors.NVApplication";
var platformRoot = path.join(rootdir, 'platforms/android');
var manifestFile = path.join(platformRoot, 'app/src/main/AndroidManifest.xml');

/* copy google-service.json */
var valuesPath = "/platforms/android/app/";
var myFile = "google-services.json";
var isJsonFileExist = path.join(rootdir, myFile);
var isDestFolderExist = path.join(rootdir, valuesPath);
var destFile = path.join(rootdir, valuesPath, myFile);

/* copy sm_push_logo.png */
var pushIconName = "sm_push_logo.png"
var pushIconPath = "platforms/android/app/src/main/res/drawable/";
var isIconExist = path.join(rootdir, pushIconName);
var isDrawableExist = path.join(rootdir, pushIconPath);
var destFile2 = path.join(rootdir, pushIconPath, pushIconName);


module.exports = function (context) {
  console.log("!*! ---------------------  * notifyvisitors * -------------------------- !*!");
  try {
    //console.log("registering application class in manifest file !!");
    if (fs.existsSync(manifestFile)) {
      fs.readFile(manifestFile, 'utf8', function (err, data) {
        if (err) {
          throw new Error('unable to find AndroidManifest.xml: ' + err);
        }
        if (data.indexOf(APPLICATION_CLASS) == -1) {
          var result = data.replace(/<application/g, '<application android:name="' + APPLICATION_CLASS + '"');
          fs.writeFile(manifestFile, result, 'utf8', function (err) {
            if (err) throw new Error('unable to write into AndroidManifest.xml: ' + err);
          })
          console.log("!*! application class registered !*!");
        } else {
          console.log("!*! application class already exist !*!");
        }
      });
    } else {
      console.log("manifest file not found !!");
    }
  } catch (e) {
    console.log("error in register application class : " + e);
  }


  try {
    //console.log("copying google-service.json file !!")
    if (fs.existsSync(isJsonFileExist)) {
      if (fs.existsSync(isDestFolderExist)) {
        fs.copyFile(isJsonFileExist, destFile, (err) => {
          if (err) throw err;
          console.log('!*! google-service.json was copied to destination !*!');
        });
      } else {
        console.log("app directory not found !!");
      }
    } else {
      console.log("google-service.json not found in root directory !!");
    }
  } catch (e) {
    console.log("error in copying google-service.json : " + e);
  }

  try {
    //console.log("copying sm_push_logo.png file !!")
    if (fs.existsSync(isIconExist)) {
      if (!fs.existsSync(isDrawableExist)) {
        fs.mkdirSync(isDrawableExist);
        fs.copyFile(isIconExist, destFile2, (err) => {
          if (err) throw err;
          console.log('!*! sm_push_logo.png was copied to destination !*!');
        });
      } else {
        fs.copyFile(isIconExist, destFile2, (err) => {
          if (err) throw err;
          console.log('!*! sm_push_logo.png was copied to destination !*!');
        });
      }
    } else {
      console.log("app icon not found in root directory");
    }

  } catch (e) {
    console.log("error in copying sm_push_logo.png : " + e);
  }
};






          