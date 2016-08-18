//
//  JBDevkeyTableViewCell.h
//  极光宝盒
//
//  Created by wuxingchen on 16/8/18.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface JBDevkeyTableViewCell : UITableViewCell

@property(nonatomic, retain)NSString *devkey;
@property (weak, nonatomic) IBOutlet UIImageView *arrow_imageView;

@end
