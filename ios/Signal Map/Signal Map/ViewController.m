//
//  ViewController.m
//  Signal Map
//
//  Created by Tapan Thaker on 25/11/12.
//  Copyright (c) 2012 Hackathon. All rights reserved.
//

#import "ViewController.h"
#import "CoreTelephony.h"
#include <dlfcn.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>



@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
   //Create a new location manager
   locationManager = [[CLLocationManager alloc] init];
    
    CTTelephonyNetworkInfo *networkInfo = [[[CTTelephonyNetworkInfo alloc] init] autorelease];
    CTCarrier *carrier = [networkInfo subscriberCellularProvider];
    
    // Get carrier name
    NSString *carrierName = [carrier carrierName];
    if (carrierName != nil)
        NSLog(@"Carrier: %@", carrierName);
  
    carrierName= carrierName.lowercaseString;
    
 
    
    switch([carrierName characterAtIndex:0])
    {
        case 'a': carrierCode=0;
        case 'b': carrierCode=1;
        case 'i': carrierCode=2;
        case 'r': carrierCode=3;
        case 't': carrierCode=4;
        case 'v': carrierCode=5;
    }
   
    
   // Set Location Manager delegate
   [locationManager setDelegate:self];
   
   // Configure permission dialog
   [locationManager setPurpose:@"My Custom Purpose Message..."];
   
    // Start updating location
    [locationManager startMonitoringSignificantLocationChanges];
    
    
    _tableView.delegate=self;
    _tableView.dataSource=self;
    
    }

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)locationManager:(CLLocationManager *)manager
didUpdateLocations:(NSArray *)locations __OSX_AVAILABLE_STARTING(__MAC_NA,__IPHONE_6_0)
{
    SignalData *lastSignalData= [SignalData MR_findFirstOrderedByAttribute:@"timestamp" ascending:NO];
    
    for(int i=0;i<[locations count];i++)
    {
        CLLocation *location =(CLLocation*) [locations objectAtIndex:i];
        double lat=location.coordinate.latitude;
        double lon=location.coordinate.longitude;
        
        NSLog(@"lat : %f long: %f",lat,lon);
        
        if(!(lastSignalData.latitude==lat && lastSignalData.longitude==lon))
        {
        SignalData *signalData=[SignalData MR_createEntity];
        signalData.latitude=lat;
        signalData.longitude=lon;
        signalData.timestamp=[[NSDate date]timeIntervalSince1970];
        signalData.strength=getSignalStrength();
        signalData.operatorid=carrierCode;
        signalData.serviceType=[self dataNetworkTypeFromStatusBar].intValue;
        }
        

    }
    
    [[NSManagedObjectContext MR_contextForCurrentThread]MR_save];
    
    [_tableView reloadData];
    NSIndexPath *indexPath = [NSIndexPath  indexPathForRow:[locationArray count]-1 inSection:0];
    [_tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    
   
}



- (void)dealloc {
    [_tableView release];
    [super dealloc];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    locationArray = [[NSArray alloc]initWithArray:[SignalData MR_findAll]];
    return [locationArray count];
}

// Row display. Implementers should *always* try to reuse cells by setting each cell's reuseIdentifier and querying for available reusable cells with dequeueReusableCellWithIdentifier:
// Cell gets various attributes set automatically based on table (separators) and data source (accessory views, editing controls)

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SignalData *signalData = [locationArray objectAtIndex:indexPath.row];
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
    }
    
    cell.textLabel.text = [NSString stringWithFormat:@"lat:%f long:%f", signalData.latitude,signalData.longitude ];
    
    cell.detailTextLabel.text = [NSString stringWithFormat:@"Strength: %f Timestamp:%d",signalData.strength,signalData.timestamp];
    
    return cell;
    
}

int getSignalStrength()
{
	void *libHandle = dlopen("/System/Library/Frameworks/CoreTelephony.framework/CoreTelephony", RTLD_LAZY);
	int (*CTGetSignalStrength)();
	CTGetSignalStrength = dlsym(libHandle, "CTGetSignalStrength");
	if( CTGetSignalStrength == NULL) NSLog(@"Could not find CTGetSignalStrength");
	int result = CTGetSignalStrength();
	dlclose(libHandle);
    
    NSLog(@"Signal Strength:%d",result);
	return result;
}

/*
0 = No wifi or cellular
1 = 2G,E and earlier? (not confirmed)
2 = 3G? (not yet confirmed)
3 = 4G
4 = LTE
5 = Wifi*/

- (NSNumber *) dataNetworkTypeFromStatusBar {
    
    UIApplication *app = [UIApplication sharedApplication];
    NSArray *subviews = [[[app valueForKey:@"statusBar"] valueForKey:@"foregroundView"]    subviews];
    NSNumber *dataNetworkItemView = nil;
    
    for (id subview in subviews) {
        if([subview isKindOfClass:[NSClassFromString(@"UIStatusBarDataNetworkItemView") class]]) {
            dataNetworkItemView = subview;
            break;
        }
    }
    NSLog(@"network type is %@",[dataNetworkItemView valueForKey:@"dataNetworkType"]);
    return [dataNetworkItemView valueForKey:@"dataNetworkType"];
}

@end
