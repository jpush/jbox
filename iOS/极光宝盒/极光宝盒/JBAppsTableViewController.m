//
//  JBAppsTableViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/26.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBAppsTableViewController.h"
#import "JBChannelTableViewCell.h"
#import "JBNetwork.h"
#import "JBChannel.h"

@interface JBAppsTableViewController ()<JBChannelTableViewCellChangeEditStyleDelegate>

@property(nonatomic, retain)NSArray *channelsArr;

@end

@implementation JBAppsTableViewController

@synthesize channelsArr = _channelsArr;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.backItem.title = @"返回";
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
}

-(void)setDevkey:(NSString *)devkey{
    _devkey = devkey;
    WEAK_SELF(weakSelf);
    [JBNetwork getChannelsWithDevkey:devkey complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        weakSelf.channelsArr = dict[@"channels"];
    }];
}

-(void)setChannelsArr:(NSArray *)channelsArr{
    _channelsArr = channelsArr;
    [self.tableView reloadData];
}

-(NSArray *)channelsArr{
    if (!_channelsArr) {
        _channelsArr = [NSArray array];
    }
    return _channelsArr;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.channelsArr.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    JBChannelTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellReuseIdentifier];
    if (!cell) {
        cell = [[NSBundle mainBundle] loadNibNamed:@"JBChannelTableViewCell" owner:nil options:nil].lastObject;
    }
    cell.arrow_imageView.hidden = YES;
    cell.delegate = self;
    JBChannel *channel = [JBChannel new];
    channel.name = self.channelsArr[indexPath.row];
    cell.channel = channel;
    return cell;
}



// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
    return UITableViewCellEditingStyleDelete;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        JBChannelTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
        if (cell.subscribed) {


        }else{

        }

    } 
}

-(void)changeEditStyle:(JBChannelTableViewCell *)cell{
    for (UIView *view in cell.subviews) {
        if ([[[view class] description] isEqualToString:@"UITableViewCellDeleteConfirmationView"]) {
            UIButton *button = view.subviews[0];
            if (!cell.subscribed) {
                button.backgroundColor = [UIColor colorWithHexString:@"ffcd00"];
                button.titleLabel.text = @"订阅";
            }else{
                button.backgroundColor = [UIColor colorWithHexString:@"ff3b30"];
                button.titleLabel.text = @"删除";
            }
        }
    }
}


/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Table view delegate

// In a xib-based application, navigation from a table can be handled in -tableView:didSelectRowAtIndexPath:
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    // Navigation logic may go here, for example:
    // Create the next view controller.
    <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:<#@"Nib name"#> bundle:nil];
    
    // Pass the selected object to the new view controller.
    
    // Push the view controller.
    [self.navigationController pushViewController:detailViewController animated:YES];
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
