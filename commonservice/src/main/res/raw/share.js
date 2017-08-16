/**
 * Created by raywu on 14-10-28.
 */
window.AlipayH5Share || (function () {
    var AlipayH5Share={};
    var shareMessage={
        title:'',
        imgUrl:'',
        desc:'',
        ready:false
    };
    //是否收集完成
    var collectReadyState={
        title:false,
        imgUrl:false,
        desc:false
    };
    var imgArr;
    var H5ShareCollector={
        init:function(){
            var t=this;
            t.collectTitle();
            t.collectDesc();
            t.collectThumbnail();
        },
        collectTitle:function(){
            var t=this;
            if(collectReadyState.title){
                return;
            }
            if(document.title && document.title.trim()!==''){
                shareMessage.title=t.contentTidy(document.title);
                collectReadyState.title=true;
                t.collectReady();
            }else if(document.getElementsByTagName('H1').length>0 && document.getElementsByTagName('H1')[0].textContent.length>0){
                //取第一个H1，且不超过32个汉字（一个中文2个字符）。
                var tmpH1=t.nodeStrFliter(document.getElementsByTagName('H1')[0]);
                if(t.getStrLen(tmpH1)<=64 && tmpH1.length>0){
                    shareMessage.title=tmpH1;
                    collectReadyState.title=true;
                    t.collectReady();
                }
            }
        },
        collectThumbnail:function(){
            var t=this;
            collectReadyState.imgUrl=false;
            t.collectReady();
            imgArr=Array.prototype.slice.call(document.images);
            t.findImgUrl();
        },
        findImgUrl:function(){
            var t=H5ShareCollector,
                limitWidth = 200,
                limitHeight = 50;
            if(imgArr.length===0){
                collectReadyState.imgUrl=true;
                t.collectReady();
                return;
            }
            if(imgArr.length>0 && !collectReadyState.imgUrl){
                for(var i=0;i<imgArr.length;i++){
                    var curImg=imgArr[i];
                    if(curImg.complete || curImg.natureWidth){
                        if(curImg.naturalWidth >= limitWidth && curImg.naturalHeight >= limitHeight){
                            shareMessage.imgUrl=curImg.src;
                            collectReadyState.imgUrl=true;
                            t.collectReady();
                            imgArr=[];
                            break;
                        }
                    }
                }
            }
        },
        collectDesc:function(){
            if(collectReadyState.desc){
                return;
            }
            var t=this,
                pArr = Array.prototype.slice.call(document.getElementsByTagName('P'));
            if(pArr.length>0){
                for(var i=0;i<pArr.length;i++){
                    var pConetent=pArr[i].textContent;
                    if(pArr[i].id=='Debug'){
                        continue;
                    }
                    //清洗之前，长度大于25汉字，小于1000个汉字
                    if(t.getStrLen(pConetent)>=50 && t.getStrLen(pConetent)<=2000){
                        pConetent=t.nodeStrFliter(pArr[i],false);
                        //清洗之后，长度大于50汉字，小于1000个汉字
                        if(t.getStrLen(pConetent)>=50 && t.getStrLen(pConetent)<=2000){
                            shareMessage.desc=pConetent;
                            collectReadyState.desc=true;
                            t.collectReady();
                            break;
                        }
                    }
                }
            }
            if(!collectReadyState.desc){
                t.travelDocument(document.body);
            }
            if(!collectReadyState.desc){
                var descNode=document.querySelector('meta[name="description"]');
                if(descNode && descNode.getAttribute('content') && descNode.getAttribute('content').trim()){
                    shareMessage.desc= t.contentTidy(descNode.getAttribute('content'));
                    collectReadyState.desc=true;
                    t.collectReady();
                }
            }
        },
        collectReady:function(){
            if(collectReadyState.title && collectReadyState.imgUrl && collectReadyState.desc){
                shareMessage.ready=true;
            }
        },
        travelDocument:function(el) {
            var t=this,
                childNodes = el.childNodes;
            if(childNodes&&childNodes.length>0){
                for (var i = 0; i < childNodes.length; i ++) {
                    var c = childNodes[i],
                        tmp;
                    switch(c.nodeType) {
                        case 1:
                            //去除p（已经尝试使用过）、无效标签（SCRIPT、STYLE）
                            if(c.nodeName!="P" && c.nodeName!='SCRIPT' && c.nodeName!='STYLE'){
                                t.travelDocument(c);
                            }
                            break;
                        case 3:
                            tmp=c.nodeValue;
                            if(t.getStrLen(tmp)>=50 && t.getStrLen(tmp)<=2000){
                                tmp=t.contentTidy(tmp);
                            }
                            if(t.getStrLen(tmp)>=50 && t.getStrLen(tmp)<=2000){
                                shareMessage.desc=tmp;
                                collectReadyState.desc=true;
                                t.collectReady();
                            }
                            break;
                    }
                    if(collectReadyState.desc){
                        break;
                    }
                }
            }
        },
        getStrLen:function(str){
            return str.replace(/[^\x00-\xff]/g, 'xx').length;
        },
        nodeStrFliter:function(element,imgAlt){
            imgAlt= imgAlt || true;
            var t=this,
                tmp=element.cloneNode(true);
            //img中的alt是否当内容
            if(imgAlt){
                Array.prototype.forEach.call(tmp.querySelectorAll('img[alt]'),function(el){
                    el.parentNode.replaceChild(document.createTextNode(el.alt),el);
                });
            }
            //清洗掉多余的script、style、link
            Array.prototype.forEach.call(tmp.querySelectorAll('script,style,link'),function(el){
                el.parentNode.replaceChild(document.createTextNode(''),el);
            });
            tmp= t.contentTidy(tmp.textContent);
            return tmp;
        },
        contentTidy:function(str){
            return str.replace(/\s{4}/g,' ').replace(/(\r|\n)/g,'').trim();
        }
    }
    if (/complete|loaded|interactive/.test(document.readyState)) {
        H5ShareCollector.init();
    } else {
        document.addEventListener('DOMContentLoaded', function (e) {
            H5ShareCollector.init();
        }, true);
    }

    AlipayH5Share.getShareContent=function(){
        H5ShareCollector.init();
        return JSON.stringify(shareMessage);
    };
    document.addEventListener('JSPlugin_AlipayH5Share',function(e){
        if(AlipayJSBridge && e.clientId){
            H5ShareCollector.init();
            AlipayJSBridge.callback(e.clientId,shareMessage);
        }
    });
    window.AlipayH5Share=AlipayH5Share;
})();