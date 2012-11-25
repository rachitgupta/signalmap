//
//  SignalData.h
//  Mouse
//
//  Created by Tapan Thaker on 25/11/12.
//  Copyright (c) 2012 Hackathon. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface SignalData : NSManagedObject

@property (nonatomic) double latitude;
@property (nonatomic) double longitude;
@property (nonatomic) int16_t operatorid;
@property (nonatomic) int16_t serviceType;
@property (nonatomic) float strength;
@property (nonatomic) int32_t timestamp;

@end
