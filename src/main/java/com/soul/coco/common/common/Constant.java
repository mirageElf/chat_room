package com.soul.coco.common.common;

/**
 * 枚举常量类
 * @author lh
 * @date 2019-9-7 14:15:37
 */
public class Constant {

    /**
     * 返回状态值
     */
    public enum RESULT{
        /**
         * 成功
         */
        CODE_YES("0"),
        /**
         * 失败
         */
        CODE_NO("-1"),
        /**
         * 失败msg
         */
        MSG_YES("操作成功"),
        /**
         * 失败msg
         */
        MSG_NO("操作失败");
        private String value;

        private RESULT(String value){
            this.value=value;
        }
        public String getValue(){
            return value;
        }
    }

}
