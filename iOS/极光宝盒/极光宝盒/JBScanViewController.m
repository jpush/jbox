//
//  JBScanViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/28.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBScanViewController.h"
#import "JBAccountViewController.h"
#import "LBXScanView.h"
#import "LBXScanResult.h"
#import "LBXScanWrapper.h"
#import "LBXAlertAction.h"

@interface JBScanViewController ()<UIImagePickerControllerDelegate,UINavigationControllerDelegate>

@end

@implementation JBScanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.backItem.title = @"返回";
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
    self.view.backgroundColor = [UIColor whiteColor];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    if (![self cameraPemission])
    {
        [self showError:@"没有摄像机权限"];
        return;
    }
}

- (BOOL)cameraPemission{
    BOOL isHavePemission = NO;
    if ([AVCaptureDevice respondsToSelector:@selector(authorizationStatusForMediaType:)])
    {
        AVAuthorizationStatus permission =
        [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];

        switch (permission) {
                case AVAuthorizationStatusAuthorized:
                isHavePemission = YES;
                break;
                case AVAuthorizationStatusDenied:
                case AVAuthorizationStatusRestricted:
                break;
                case AVAuthorizationStatusNotDetermined:
                isHavePemission = YES;
                break;
        }
    }

    return isHavePemission;
}

- (void)scanResultWithArray:(NSArray<LBXScanResult*>*)array{

    if (array.count < 1)
    {
        [self popAlertMsgWithScanResult:nil];

        return;
    }

    //经测试，可以同时识别2个二维码，不能同时识别二维码和条形码
    for (LBXScanResult *result in array) {

        NSLog(@"scanResult:%@",result.strScanned);
    }

    LBXScanResult *scanResult = array[0];

    NSString*strResult = scanResult.strScanned;

    self.scanImage = scanResult.imgScanned;

    if (!strResult) {

        [self popAlertMsgWithScanResult:nil];

        return;
    }

    //震动提醒
    // [LBXScanWrapper systemVibrate];
    //声音提醒
    [LBXScanWrapper systemSound];

    [self showScanResultViewController:scanResult];

}

- (void)popAlertMsgWithScanResult:(NSString*)strResult{
    if (!strResult) {
        strResult = @"识别失败";
    }
    WEAK_SELF(weakSelf);
    [LBXAlertAction showAlertWithTitle:@"扫码内容" msg:strResult chooseBlock:^(NSInteger buttonIdx) {
        //点击完，继续扫码
        [weakSelf reStartDevice];
    } buttonsStatement:@"知道了",nil];
}

- (void)showScanResultViewController:(LBXScanResult*)strResult{
    JBAccountViewController *controller = self.navigationController.viewControllers[1];
    controller.scanString   = strResult.strScanned;
    controller.scanCodeType = strResult.strBarCodeType;
    [self.navigationController popToRootViewControllerAnimated:YES];
}

@end
