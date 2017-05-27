//
//  JBSlideView2.h
//  jbox
//
//  Created by wuxingchen on 2016/10/25.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface JBTeamSlideView : UIView <UITableViewDelegate, UITableViewDataSource>

@property(nonatomic, retain)NSMutableArray *devArray;
@property (weak, nonatomic) IBOutlet UITableView *team_tableView;

@end
