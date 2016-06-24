/**
 *  Copyright 2014-2016 CyberVision, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

//
//  AppDelegate.h
//  SmartHouse
//
//  Created by Anton Bohomol on 4/3/15.
//  Copyright (c) 2015 CYBERVISION INC. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "KaaController.h"
#import "MusicDeviceController.h"
#import "PhotoDeviceController.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property(strong,nonatomic) UIWindow *window;
@property(strong,nonatomic) KaaController *kaaController;
@property(strong,nonatomic) MusicDeviceController *musicDeviceController;
@property(strong,nonatomic) PhotoDeviceController *photoDeviceController;

- (void)storeUserName:(NSString *) userName;
- (NSString *)loadUserName;

- (void)storeUserPass:(NSString *) password;
- (NSString *)loadUserPass;

@end

