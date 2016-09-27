//
//  JBScanViewController.m
//  极光宝盒
//
//  Created by wuxingchen on 16/7/28.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBScanViewController.h"
#import "JBDevManageViewController.h"
#import "LBXScanView.h"
#import "LBXScanResult.h"
#import "LBXScanWrapper.h"
#import "LBXAlertAction.h"
#import <AVFoundation/AVFoundation.h>

@interface JBScanViewController ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@end

@implementation JBScanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.backItem.title = @"返回";
    self.title = @"二维码";
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"相册" style:UIBarButtonItemStyleDone target:self action:@selector(showAlbum)];

    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];

    LBXScanViewStyle *style = [[LBXScanViewStyle alloc]init];
    style.centerUpOffset = 60/667.0*UIScreenHeight;
    style.xScanRetangleOffset = 80/375.0*UIScreenWidth;
    style.alpa_notRecoginitonArea = 0.6;
    style.photoframeAngleStyle = LBXScanViewPhotoframeAngleStyle_Inner;
    style.photoframeLineW = 3.0;
    style.photoframeAngleW = 36.0;
    style.photoframeAngleH = 36.0;
    style.colorAngle = [UIColor colorWithHexString:@"1feaff"];
    style.alpa_notRecoginitonArea = 0.5;
    style.isNeedShowRetangle = NO;
    style.anmiationStyle = LBXScanViewAnimationStyle_NetGrid;
    self.style = style;

}

-(void)showAlbum{
    UIImagePickerController *picker = [[UIImagePickerController alloc]init];
    picker.view.backgroundColor = [UIColor orangeColor];
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    picker.delegate = self;
    picker.allowsEditing = YES;
    [self presentViewController:picker animated:YES completion:nil];
}

-(void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info{
    NSString *type = [info objectForKey:UIImagePickerControllerMediaType];
    if ([type isEqualToString:@"public.image"]){
        UIImage* image = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
        NSData *data;
        if (UIImagePNGRepresentation(image) == nil){
            data = UIImageJPEGRepresentation(image, 1.0);
        }else{
            data = UIImagePNGRepresentation(image);
        }
        WEAK_SELF(weakSelf);
        [picker dismissViewControllerAnimated:YES completion:^{
            [LBXScanWrapper recognizeImage:image success:^(NSArray<LBXScanResult *> *array) {
                [weakSelf scanResultWithArray:array];
            }];
        }];
    }
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
    CGRect scanFram = CGRectMake(80/375.0*UIScreenWidth, 60/667.0*UIScreenHeight, UIScreenWidth - 2 * 80/375.0*UIScreenWidth, UIScreenWidth - 2 * 80/375.0*UIScreenWidth);
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(scanFram.origin.x, scanFram.origin.y + scanFram.size.height + 50, scanFram.size.width, 30)];
    label.text = @"扫一扫添加 channel";
    label.textAlignment = NSTextAlignmentCenter;
    label.font = [UIFont systemFontOfSize:17];
    label.textColor = [UIColor whiteColor];
    [self.qRScanView addSubview:label];
    [self.qRScanView bringSubviewToFront:label];
}

- (BOOL)cameraPemission{
    BOOL isHavePemission = NO;
    if ([AVCaptureDevice respondsToSelector:@selector(authorizationStatusForMediaType:)]){
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

    if (array.count < 1){
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
    JBDevManageViewController *controller = [[JBDevManageViewController alloc] initWithNibName:@"JBDevManageViewController" bundle:[NSBundle mainBundle]];
    controller.scanedDevkey = [strResult.strScanned copy];
    [self.navigationController pushViewController:controller animated:YES];
}

@end
