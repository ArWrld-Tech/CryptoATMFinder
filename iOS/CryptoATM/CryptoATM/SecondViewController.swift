//
//  SecondViewController.swift
//  CryptoATM
//
//  Created by David Hodge on 12/11/17.
//  Copyright © 2017 David Hodge. All rights reserved.
//

import UIKit
import Parse
import WebKit

class SecondViewController: UIViewController, UIWebViewDelegate {
    
    @IBOutlet weak var webView: WKWebView!
    //    @IBOutlet weak var webView: WKWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let url = URL(string: "https://coinranking.com/" as! String) {
            let request = URLRequest(url: url)
            self.webView.load(request);
        }
    }
}

