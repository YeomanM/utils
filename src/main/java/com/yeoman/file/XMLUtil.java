package com.yeoman.file;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @Description :
 * ---------------------------------
 * @Author : Yeoman
 * @Date : Create in 2018/8/1
 */
public class XMLUtil {

    public static JSONObject xmlStringToJson(String xml){
        //开始解析xml格式的内容
        Document document = null;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        //ancestors用于存储当前节点的直系祖先节点
        //traverse用于存储xml中待遍历的节点
        Stack<Node> ancestors = new Stack<>();
        Stack<List<Element>> traverse = new Stack<>();
        //先获取根节点
        String text = null;
        Element root = document.getRootElement();
        List<Element> children = root.elements();
        Node node = new Node();
        node.setNodeName(root.getName());
        if ((text = root.getText().trim()) != null && !text.equals(""))
            node.setNodeValue(text);
        //将根节点及其直系子代（个数不为0时）加入栈中，用于遍历
        //children这里是根节点的儿子，node是根节点，即是所有children的祖先
        ancestors.push(node);
        if (!children.isEmpty()){
            traverse.push(children);
        }

        //开始遍历xml中第二层的数据
        List<Element> flag = null;
        Element element = null;
        Node parent = null;
        //先查看待遍历中是否有数据
        while (!traverse.empty()){
            flag = traverse.pop();
            parent = ancestors.pop();
            //遍历一个亲兄弟子代的列表，parent是他们的父亲
            while (!flag.isEmpty()){
                element = flag.remove(0);
                node = new Node();
                node.setNodeName(element.getName());
                if ((text = element.getText().trim()) != null && !text.equals(""))
                    node.setNodeValue(text);
                parent.addChildren(node);
                children = element.elements();
                //当此时的节点的后代不为空时，先遍历改节点的后代
                //先后将该节点的所有未遍历的兄弟节点和所有子节点加入待遍历节点的栈中
                //先后将该节点的父节点以及该节点加入祖先节点栈中
                //此处该节点没有未遍历兄弟节点的情况
                if (!children.isEmpty()){
                    traverse.push(flag);
                    ancestors.push(parent);
                    traverse.push(children);
                    ancestors.push(node);
                    break;
                }
            }
        }
        return (JSONObject) JSONObject.toJSON(parent);
    }


    static class Node {

        private String nodeName;
        private String nodeValue;
        private List<Node> children;

        public synchronized void addChildren(Node node) {
            if (this.children == null) {
                children = new ArrayList<>();
            }
            children.add(node);
        }

        public String getNodeName() {
            return nodeName;
        }

        public String getNodeValue() {
            return nodeValue;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }
        public void setNodeValue(String nodeValue) {
            this.nodeValue = nodeValue;
        }
    }

}
