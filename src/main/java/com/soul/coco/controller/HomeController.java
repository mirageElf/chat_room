package com.soul.coco.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.soul.coco.common.mvc.BaseController;
import com.soul.coco.common.utils.ImgToCharacterUtils;
import com.soul.coco.common.utils.Result;
import com.soul.coco.common.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@RestController
public class HomeController extends BaseController {

    /**
     * 将图片进行字符转换
     * @param file
     * @return
     */
    @RequestMapping("/uploadImg")
    public Result uploadImg(@RequestParam("file") MultipartFile file, @RequestParam("ratio") BigDecimal ratio) {
        if (file.isEmpty()) {
            return Result.error("文件不存在");
        }
        if (BigDecimal.ZERO.compareTo(ratio) != -1 || new BigDecimal(100).compareTo(ratio) == -1) {
            // 如果图片缩放比例不在1-100间 则默认100
            ratio = new BigDecimal(100);
        }
        try {
            // 获取文件inputStream流
            InputStream inputStream = file.getInputStream();
            FileInputStream fileInputStream = (FileInputStream) inputStream;

            // 将inputStream流转FileInputStream流进行字符转换
            String imgTxt = ImgToCharacterUtils.imgToString(fileInputStream, ratio);
            if (StringUtils.isBlank(imgTxt)) {
                return Result.error("转换失败...");
            }

            String uuid = Utils.uuid();
            // 生成uuid将转换的字符保存再redis中。设置时效时间为3分钟(单位秒)
            redisUtil.setString(uuid, imgTxt, 120);
            return Result.ok().put("id", uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("失败了...o(╥﹏╥)o");
    }

    /**
     * 根据key从redis中获取字符下载
     * @param id
     * @param request
     * @param response
     */
    @RequestMapping("/downloadTxt")
    public void downloadTxt(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isNotBlank(id)) {
            try {
                // id不为空，根据key从redis中获取字符，进行下载
                String imgTxt = redisUtil.getString(id);
                // 取完值删除
                redisUtil.del(id);
                ImgToCharacterUtils.exportTxt(request, response, "字符画.txt", imgTxt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
