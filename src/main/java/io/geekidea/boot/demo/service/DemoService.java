package io.geekidea.boot.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.geekidea.boot.demo.dto.DemoDto;
import io.geekidea.boot.demo.entity.Demo;
import io.geekidea.boot.demo.query.DemoAppQuery;
import io.geekidea.boot.demo.query.DemoQuery;
import io.geekidea.boot.demo.vo.DemoAppVo;
import io.geekidea.boot.demo.vo.DemoVo;
import io.geekidea.boot.framework.page.Paging;


/**
 * 演示 服务接口
 *
 * @author geekidea
 * @since 2023-12-09
 */
public interface DemoService extends IService<Demo> {

    /**
     * 添加演示
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean addDemo(DemoDto dto) throws Exception;

    /**
     * 修改演示
     *
     * @param dto
     * @return
     * @throws Exception
     */
    boolean updateDemo(DemoDto dto) throws Exception;

    /**
     * 删除演示
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteDemo(Long id) throws Exception;

    /**
     * 演示详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    DemoVo getDemoById(Long id) throws Exception;

    /**
     * 演示分页列表
     *
     * @param query
     * @return
     * @throws Exception
     */
    Paging<DemoVo> getDemoPage(DemoQuery query) throws Exception;

    /**
     * App演示详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    DemoAppVo getAppDemoById(Long id) throws Exception;

    /**
     * App演示分页列表
     *
     * @param query
     * @return
     * @throws Exception
     */
    Paging<DemoAppVo> getAppDemoPage(DemoAppQuery query) throws Exception;

}
