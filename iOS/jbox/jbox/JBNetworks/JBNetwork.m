//
//  JBNetwork.m
//  极光宝盒
//
//  Created by wuxingchen on 16/8/1.
//  Copyright © 2016年 57380422@qq.com. All rights reserved.
//

#import "JBNetwork.h"
#import <AFNetworking.h>
#import "JBDatabase.h"

NSString *const base_url = @"http://jbox.jiguang.cn/v1/developers/";

#define IsReachable [AFNetworkReachabilityManager sharedManager].isReachable
#define StrBy(a,b) [NSString stringWithFormat:@"%@%@", a, b]

@implementation JBNetwork

typedef NS_ENUM(NSInteger, RequestHttpType){
    RequestHttpTypePOST = 1,
    RequestHttpTypeGET,
};

//--------------------------------------- public ---------------------------------------//

#pragma mark - public

+(void)getDevInfoWithDevkey:(NSString*)devkeyStr complete:(void (^)(JBDevkey *devkey))complete{
    [JBNetwork GET:devkeyStr paramtes:devkeyStr complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        JBDevkey *devkey = [JBDevkey new];
        [devkey setValuesForKeysWithDictionary:dict];
        complete(devkey);
    }];
}

+(void)getChannelsWithDevkey:(NSString*)devkey complete:(void (^)(id responseObject))complete{
    [JBNetwork GET:StrBy(devkey, @"/channels") paramtes:devkey complete:^(id responseObject) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingAllowFragments error:nil];
        NSMutableArray *downloadChannelNames = dict[@"channels"];
        //把数据库里有，但是下载没有的数据删掉
        NSArray *storedChannels = [JBDatabase getChannelsFromDevkey:devkey];
        NSMutableArray *storedChannelNames = [NSMutableArray array];
        for (JBChannel *channel in storedChannels) {
            [storedChannelNames addObject:channel.name];
        }
        [storedChannelNames removeObjectsInArray:downloadChannelNames];
        for (NSString *name in storedChannelNames) {
            JBChannel *channel = [JBChannel new];
            channel.name = name;
            channel.dev_key = devkey;
            [JBDatabase deleteChannel:channel];
        }

        NSMutableArray *channels = [NSMutableArray array];
        for (NSString *name in downloadChannelNames) {
            JBChannel *channel   = [JBChannel new];
            channel.name         = name;
            channel.isSubscribed = @"0";
            channel.dev_key      = devkey;
            [channels addObject:channel];
        }
        [JBDatabase createChannels:channels];
        complete(responseObject);
    }];
}

//--------------------------------------- private ---------------------------------------//

#pragma mark - private

+(void)POST:(NSString *)urlStr
   paramtes:(id)param
       body:(NSData*)body
   complete:(void (^)(id responseObject))complete{
    [self requestWithUrl:urlStr paramtes:param type:RequestHttpTypePOST body:body complete:^(id responseObject) {
        complete(responseObject);
    }];
}

+(void)GET:(NSString *)urlStr
   paramtes:(id)param
   complete:(void (^)(id responseObject))complete{
    [self requestWithUrl:urlStr paramtes:param type:RequestHttpTypeGET body:nil complete:^(id responseObject) {
        complete(responseObject);
    }];
}

+(NSURLSessionDataTask *)requestWithUrl:(NSString *)urlStr
                               paramtes:(id)param
                                   type:(RequestHttpType)type
                                   body:(NSData*)body
                               complete:(void (^)(id responseObject))complete{
    if (IsReachable){
        NSLog(@"网断，再试");
        if (complete){
            complete(nil);
        }
        return nil;
    }

    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;

    NSString *requestType = (type == RequestHttpTypeGET ? @"GET" : @"POST");
    AFHTTPRequestSerializer *requestSerializer = [AFHTTPRequestSerializer serializer];

    NSString *authStr   = [NSString stringWithFormat:@"%@:%@", @"1c29cb5814072b5b1f8ef829", param];
    NSData   *authData  = [authStr dataUsingEncoding:NSUTF8StringEncoding];
    NSString *authValue = [NSString stringWithFormat:@"Basic %@", [authData base64EncodedStringWithOptions:0]];

    [requestSerializer setValue:authValue forHTTPHeaderField:@"Authorization"];
//    [requestSerializer setValue:@"hVkbyLdeA7K0Cm9BUgY6" forHTTPHeaderField:@"dev_key"];
    [requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];

    NSString *requestUrlStr = [NSString stringWithFormat:@"%@%@",base_url,urlStr];
    NSMutableURLRequest *request = [requestSerializer requestWithMethod:requestType URLString:requestUrlStr parameters:param error:nil];

    request.HTTPBody = body;

    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];

    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:configuration];

    AFHTTPResponseSerializer *responseSerializer = [AFHTTPResponseSerializer serializer];
    responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"charset=utf-8", nil];
    responseSerializer.acceptableStatusCodes = [NSIndexSet indexSetWithIndex:200];

    manager.responseSerializer = responseSerializer;

    NSURLSessionDataTask *dataTask = [manager dataTaskWithRequest:request completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {

        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

        if (!error){
            complete(responseObject);
        }

    }];

    // 开始请求
    [dataTask resume];
    //    [dataTask cancel];
    
    return dataTask;
}

@end
