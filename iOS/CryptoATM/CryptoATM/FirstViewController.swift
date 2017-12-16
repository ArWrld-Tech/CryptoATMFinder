//
//  FirstViewController.swift
//  CryptoATM
//
//  Created by David Hodge on 12/11/17.
//  Copyright © 2017 David Hodge. All rights reserved.
//

import UIKit
import Mapbox
import Parse
class MyCustomPointAnnotation: MGLPointAnnotation {
    var willUseImage: Bool = false
}

class FirstViewController:  UIViewController, MGLMapViewDelegate {
    
    var mapView: MGLMapView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let styleURL = NSURL(string:"mapbox://styles/cartoadmin/cjaypif2h4emp2rpdufz9a2mk")
        self.mapView = MGLMapView(frame: self.view.bounds,  styleURL: styleURL as URL?)
        self.mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        self.view.addSubview(self.mapView)
        self.mapView.delegate = self
        
        let allAnnotations = self.mapView.annotations
        if allAnnotations != nil{
            self.mapView.removeAnnotations(allAnnotations!)
        }
    }
    
    // Use the default marker. See also: our view annotation or custom marker examples.
    func mapView(_ mapView: MGLMapView, viewFor annotation: MGLAnnotation) -> MGLAnnotationView? {
        
        if let castAnnotation = annotation as? MyCustomPointAnnotation {
            if (castAnnotation.willUseImage) {
                return nil;
            }
        }
        
        // Assign a reuse identifier to be used by both of the annotation views, taking advantage of their similarities.
        let reuseIdentifier = "reusableDotView"
        
        // For better performance, always try to reuse existing annotations.
        var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: reuseIdentifier)
        
        // If there’s no reusable annotation view available, initialize a new one.
        if annotationView == nil {
            annotationView = MGLAnnotationView(reuseIdentifier: reuseIdentifier)
            annotationView?.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
            annotationView?.layer.cornerRadius = (annotationView?.frame.size.width)! / 2
            annotationView?.layer.borderWidth = 4.0
            annotationView?.layer.borderColor = UIColor.white.cgColor
            annotationView!.backgroundColor = UIColor(red:0.03, green:0.80, blue:0.69, alpha:1.0)
        }
        
        return annotationView
    }
    
    func mapView(_ mapView: MGLMapView, didSelect annotation: MGLAnnotation) {
        let camera = MGLMapCamera(lookingAtCenter: annotation.coordinate, fromDistance: 1750, pitch: self.mapView.camera.pitch, heading: self.mapView.camera.heading)
        mapView.fly(to: camera, completionHandler: nil)
    }
    
    // Allow callout view to appear when an annotation is tapped.
    func mapView(_ mapView: MGLMapView, annotationCanShowCallout annotation: MGLAnnotation) -> Bool {
        return true
    }
    
    // This delegate method is where you tell the map to load an image for a specific annotation based on the willUseImage property of the custom subclass.
    func mapView(_ mapView: MGLMapView, imageFor annotation: MGLAnnotation) -> MGLAnnotationImage? {
        
        if let castAnnotation = annotation as? MyCustomPointAnnotation {
            if (!castAnnotation.willUseImage) {
                return nil;
            }
        }
        
        // For better performance, always try to reuse existing annotations.
        var annotationImage = mapView.dequeueReusableAnnotationImage(withIdentifier: "marker")
        
        // If there is no reusable annotation image available, initialize a new one.
        if(annotationImage == nil) {
            annotationImage = MGLAnnotationImage(image: UIImage(named: "marker")!, reuseIdentifier: "marker")
        }
        
        return annotationImage
    }
    
    func mapView(_ mapView: MGLMapView, tapOnCalloutFor annotation: MGLAnnotation) {
        print("tap on callout")
        print(annotation.subtitle);
    }
    
    func mapView(_ mapView: MGLMapView, didFinishLoading style: MGLStyle) {
        
        // Access the Mapbox Streets source and use it to create a `MGLFillExtrusionStyleLayer`. The source identifier is `composite`. Use the `sources` property on a style to verify source identifiers.
        if let source = style.source(withIdentifier: "composite") {
            let layer = MGLFillExtrusionStyleLayer(identifier: "buildings", source: source)
            layer.sourceLayerIdentifier = "building"
            
            // Filter out buildings that should not extrude.
            layer.predicate = NSPredicate(format: "extrude == 'true' AND height >= 0")
            
            // Set the fill extrusion height to the value for the building height attribute.
            layer.fillExtrusionHeight = MGLStyleValue(interpolationMode: .identity, sourceStops: nil, attributeName: "height", options: nil)
            layer.fillExtrusionBase = MGLStyleValue(interpolationMode: .identity, sourceStops: nil, attributeName: "min_height", options: nil)
            layer.fillExtrusionOpacity = MGLStyleValue(rawValue: 0.75)
            layer.fillExtrusionColor = MGLStyleValue(rawValue: .darkGray)
            
            // Insert the fill extrusion layer below a POI label layer. If you aren’t sure what the layer is called, you can view the style in Mapbox Studio or iterate over the style’s layers property, printing out each layer’s identifier.
            if let symbolLayer = style.layer(withIdentifier: "poi-scalerank3") {
                style.insertLayer(layer, below: symbolLayer)
            } else {
                style.addLayer(layer)
            }
        }
        
//        let camera = MGLMapCamera(lookingAtCenter: CLLocationCoordinate2D(latitude: center.latitude, longitude: center.longitude), fromDistance: 4500, pitch: 15, heading: 180)
        // Animate the camera movement over 5 seconds.
//        mapView.setCamera(camera, animated: true)
        
        self.mapView.showsUserLocation = true
        let queryAttr = PFQuery(className: "atm");
        queryAttr.findObjectsInBackground{
            (objects: [PFObject]??, error: Error?) -> Void in
            if error != nil || objects == nil {
                print("The getFirstObject request failed.")
            } else {
                
                for item in (objects as? [PFObject])! {
                    let name = item["name"] as! String;
                    let point = item["loc"] as! PFGeoPoint;
                    print("Map: " + name);
                    let hello = MyCustomPointAnnotation()
                    hello.coordinate = CLLocationCoordinate2D(latitude: point.latitude, longitude: point.longitude);
                    hello.title = name
                    hello.subtitle = item.objectId;
                    hello.willUseImage = true
                    self.mapView.addAnnotation(hello)
                }
            }
        }
    }
    
    func hexStringToUIColor (hex:String) -> UIColor {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        
        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }
        
        if ((cString.characters.count) != 6) {
            return UIColor.gray
        }
        
        var rgbValue:UInt32 = 0
        Scanner(string: cString).scanHexInt32(&rgbValue)
        
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}

