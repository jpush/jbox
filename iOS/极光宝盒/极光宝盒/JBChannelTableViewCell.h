//
//  JBChannelTableViewCell.h
//  极光宝盒
//
//  Created by wuxingchen on 16/7/20.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@class JBChannelTableViewCell;

@protocol JBChannelTableViewCellChangeEditStyleDelegate
-(void)changeEditStyle:(JBChannelTableViewCell*)cell;
@end

@interface JBChannelTableViewCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *channel_label;
@property (weak, nonatomic) IBOutlet UIImageView *arrow_imageView;
@property (weak, nonatomic) IBOutlet UILabel *title_label;
@property (nonatomic, assign)BOOL subscribed;
@property (nonatomic, assign)id <JBChannelTableViewCellChangeEditStyleDelegate>delegate;

@end

