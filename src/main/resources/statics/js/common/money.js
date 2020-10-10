//var priceReg = /(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/;
var priceReg = /((^[1-9]\d*)|^0)(\.\d{0,2}){0,1}$/

/**
 * 校验金额 最多为两位小数
 * @param val
 * @returns {boolean}
 */
function priceCheck(val) {
    if (!priceReg.test(val)){
        return false;
    }
    return true;
}