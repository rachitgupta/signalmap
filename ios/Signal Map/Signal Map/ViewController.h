//
//  ViewController.h
//  Signal Map
//
//  Created by Tapan Thaker on 25/11/12.
//  Copyright (c) 2012 Hackathon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import "SignalData.h"

@interface ViewController : UIViewController<CLLocationManagerDelegate,UITableViewDataSource,UITableViewDelegate> {
    CLLocationManager *locationManager;
    
    NSArray *locationArray;
       int carrierCode ;
   
}
@property (retain, nonatomic) IBOutlet UITableView *tableView;

@end
