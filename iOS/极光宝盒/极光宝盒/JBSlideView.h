//
//  JBSlideView.h
//  极光宝盒
//
//  Created by wuxingchen on 16/9/12.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface JBSlideView : UIView <UITableViewDelegate, UITableViewDataSource>
-(void)shouldUpdate;
@property (weak, nonatomic) IBOutlet UITableView *channel_tableView;
@property(nonatomic, assign)BOOL isEditing;

@end
