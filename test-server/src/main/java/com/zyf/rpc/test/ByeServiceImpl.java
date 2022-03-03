package com.zyf.rpc.test;

import com.zyf.rpc.annotation.Service;
import com.zyf.rpc.api.ByeService;

/**
 * @author zyf
 * @date 2022/3/3 23:53
 * @description 服务实现类
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye," + name;
    }

}
