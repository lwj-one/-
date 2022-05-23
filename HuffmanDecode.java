package copy.huffmantree;

import java.io.*;
import java.util.*;

/**
 * 一.把String字符转成ascii编码然后通过赫夫曼树生成唯一标识符。
 *
 *
 */
public class HuffmanDecode {
    public static void main(String[] args) {
       //
        String srcFile="E://src2.bmp";
        String dstFile="E://dst2.zip";
        //1、zipFile方法是压缩
        zipFile(srcFile,dstFile);
        System.out.println("压缩文件成功");
        //2、unZipFile是解压
//         unZipFile(dstFile,srcFile);
//        System.out.println("解压成功");
//        String content = "i like like like java do you like a java";
//        byte[] contentBytes = content.getBytes();
//        System.out.println(Arrays.toString(contentBytes));
//        byte[] huffmanCodesBytes = huffmanZip(contentBytes);
//        System.out.println("压缩后的编码"+Arrays.toString(huffmanCodesBytes));
//        //步骤5：解码的过程
//       deCode(huffmanCodes,huffmanCodesBytes);
//        byte[] sourceBytes = deCode(huffmanCodes, huffmanCodesBytes);
//        System.out.println(new String(sourceBytes));
        //getByte把字符变成字节ASCII码
//        System.out.println("1、getByte把字符变成字节ASCII码");
//        System.out.println(Arrays.toString(contentBytes));
//        List<Node1> nodes=getNodes(contentBytes);
//        System.out.println("2、把字节码按照个数转化权值遍历到List的Node结点中");
//        System.out.println(nodes);
//        System.out.println("3、创建赫夫曼树");
//        Node1 huffmanTree = createHuffmanTree(nodes);
//        preOrder(huffmanTree);
//        System.out.println("4、生成赫夫曼树对应的赫夫曼编码");
//        getCodes(huffmanTree,"",huffmanRoute);
//        System.out.println(huffmanCodes);
//        zip(contentBytes,huffmanCodes);


    }
    public static void unZipFile(String zipFile,String dstFile){
        InputStream is=null;
        ObjectInputStream ois=null;
        OutputStream os=null;
        try {
            // 初始化
            is = new FileInputStream(zipFile);
            // 初始化对象输入流 --- 用于读入 赫夫曼表和源文件数组
            ois = new ObjectInputStream(is);
            byte[] huffmanBytes = (byte[])ois.readObject();
            Map<Byte,String> huffmanCodes = (Map<Byte,String>)ois.readObject();

            // 解码
            byte[] bytes = deCode(huffmanCodes, huffmanBytes);
            // 将bytes 数组写入到目标文件
            os = new FileOutputStream(dstFile);
            os.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
                ois.close();
                is.close();
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
                System.out.println("赫夫曼树对应的权重");
            }
        }
    }

    public static void  zipFile(String srcFile,String dstFile){

        FileInputStream is=null;
        OutputStream os=null;
        ObjectOutputStream oos=null;
        try {
            //创建文件的输入流
            is = new FileInputStream(srcFile);
            //创建一个源文件大小一样的byte[]
            byte[] b=new byte[is.available()];
            //读取文件
            is.read(b);
            //对源文件进行压缩
            byte[] huffmanBytes = huffmanZip(b);
            //创建文件的输出流，存放压缩文件
            os=new FileOutputStream(dstFile);
            //创建一个和文件相关的ObjectOutputStrem
            oos=new ObjectOutputStream(os);
            //我们以对象流的方式写入赫夫曼的编码
            oos.writeObject(huffmanBytes);
            //这里我们就以对象流的方式写入赫夫曼编码，为了方便我们以后恢复源文件时使用
            //注意一定要把赫夫曼编码写入源文件。
            oos.writeObject(huffmanCodes);


        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            try {
                is.close();
                oos.close();
                os.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }


    /**
     *
     * @param huffmanCodes
     * @param huffmanBytes
     * @return
     */
      private  static byte[] deCode(Map<Byte,String> huffmanCodes, byte[] huffmanBytes){
          StringBuilder stringBuilder=new StringBuilder();
          for (int i = 0; i < huffmanBytes.length; i++) {
              byte bytes=huffmanBytes[i];
              boolean flag=(i==huffmanBytes.length-1);
              stringBuilder.append(byteToBitString(!flag,bytes));
          }
          System.out.println(stringBuilder.toString());
          //反向地图，通过二进制得到字符。
          Map<String,Byte> map=new HashMap<>();
          for (Map.Entry<Byte,String> entry: huffmanCodes.entrySet()){
              map.put(entry.getValue(),entry.getKey());
          }
          System.out.println(map);
          List<Byte> list=new ArrayList<>();
          for (int i = 0; i < stringBuilder.length();) {
              int count=1;
              Byte b=null;
              boolean flag=true;
              while (flag){
                   String key=stringBuilder.substring(i,i+count);
                   b=map.get(key);
                   if (b==null){
                       count++;
                   }else {
                       flag=false;
                   }
              }
              list.add(b);
              i+=count;
          }
          byte[] bytes=new byte[list.size()];
          for (int i = 0; i < bytes.length; i++) {
              bytes[i]=list.get(i);
          }
          return bytes;
      }

    /**
     * 将byte转成二进制（补码）
     * @param flag  表示要不要补高位
     * @param b     传入的数组，也就是二进制（补码）
     * @return
     */

    private static String byteToBitString(boolean flag,byte b) {
        int temp = b;
        //因为正数的补码为自己，所以会造成返回值越界的情况
        if (flag) {
            temp |= 256;
        }
        //这里转换的是二进制的补码，Integer.toBinaryString。
        String str = Integer.toBinaryString(temp);
        if (flag) {
            return str.substring(str.length() - 8);
        } else{
            return str;
         }
    }


    public static byte[] huffmanZip(byte[] bytes) {
        List<Node1> nodes = getNodes(bytes);
        // 根据 nodes 创建的赫夫曼树
        Node1 huffmanTreeRoot = createHuffmanTree(nodes);
        // 对应的赫夫曼编码(根据 赫夫曼树)
        Map<Byte,String> huffmanCodes = getCodes(huffmanTreeRoot);
        // 根据生成的赫夫曼编码，压缩得到压缩后的赫夫曼编码字节数组
        return zip(bytes,huffmanCodes);
    }
    //步骤4：拿到了赫夫曼二进制编码返回的字节数组
    private static byte[] zip(byte[] bytes ,Map<Byte,String> huffmanCodes){
        //1、利用huffmanCodes 将bytes转变成赫夫曼对应的字符串
        StringBuilder stringBuilder=new StringBuilder();
        //遍历bytes数组
        for (byte b : bytes) {
            //get对应的二进制键得到String值
            stringBuilder.append(huffmanCodes.get(b));
        }
//        System.out.println("测试"+stringBuilder);
        int len=(stringBuilder.length()+7)/8;
        byte[] by=new byte[len];
        int index=0;
        for (int i = 0; i < stringBuilder.length(); i+=8) {
            String strByte;
            if (i+8>stringBuilder.length()){
                strByte=stringBuilder.substring(i);
          }else {
                strByte=stringBuilder.substring(i,i+8);
          }
            //by数组的值是二进制补码转换成的十进制。
           by[index]=(byte) Integer.parseInt(strByte,2);
           index++;
        }
        return by;
    }


    // 步骤3：生成赫夫曼树对应的赫夫曼编码
    // 思路：1、将赫夫曼编码表存放到 Map<Byte,String>
    // eg：生成的赫夫曼编码表{32=01, 97=100, 100=11000, 117=11001, 101=1110, 118=11011, 105=101, 121=11010, 106=0010, 107=1111, 108=000, 111=0011}
    static Map<Byte,String> huffmanCodes =new HashMap<>();
    //这个 huffmanRoute
    static StringBuilder huffmanRoute = new StringBuilder();

   private static void getCodes(Node1 nodes,String string,StringBuilder stringBuilder){
       StringBuilder stringBuilder1 = new StringBuilder(stringBuilder);
       stringBuilder1.append(string);
       if (nodes!=null){
           if (nodes.data==null){
               getCodes(nodes.left,"0",stringBuilder1);
               getCodes(nodes.right,"1",stringBuilder1);
           }else {
               huffmanCodes.put(nodes.data,stringBuilder1.toString());
           }
       }

   }
    public static Map<Byte, String> getCodes(Node1 root) {
        if(root==null) {
            return null;
        }
        // 处理 root 的左子树
        getCodes(root.left, "0", huffmanRoute);
        // 处理 root 的右子树
        getCodes(root.right, "1", huffmanRoute);

        return huffmanCodes;
    }

    //2、将ASCII码依次放进集合，字节数组 byte[] 转为 节点集合 List，用于后面构建赫夫曼树。
    private static List<Node1> getNodes(byte[] bytes){
        //(1)创建两个集合依次用来Map用来计算每个字节出现的次数，然后把key跟value依次存入List。
        ArrayList<Node1> nodes = new ArrayList<>();
        Map<Byte, Integer> counts = new HashMap<>();
        for (byte b : bytes) {
             //map集合的key得到map的value这一步不好理解
             Integer count= counts.get(b);
             if (count==null){
                 counts.put(b,1);
             }else {
                 counts.put(b,count+1);
             }
        }
        for (Map.Entry<Byte,Integer> entry:counts.entrySet()){
            nodes.add(new Node1(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }
    //2.1前序遍历
    private static void preOrder(Node1 root) {
        if (root != null) {
            root.preOrder();
        } else {
            System.out.println("树为空");
        }
    }
    //1、创建赫夫曼树。
    private static Node1 createHuffmanTree(List<Node1> nodes){
            while (nodes.size()>1){
                //排序后放进了集合
                Collections.sort(nodes);
                //因为是有序的所以左结点必定是0小
                Node1 leftNode= nodes.get(0);
                Node1 rightNode=nodes.get(1);
                Node1 parent=new Node1(null, leftNode.weight+rightNode.weight);
                parent.left=leftNode;
                parent.right=rightNode;
                nodes.remove(leftNode);
                nodes.remove(rightNode);
                nodes.add(parent);
            }
            return nodes.get(0);
    }

}

class Node1 implements Comparable<Node1>{
    Byte data;//存放字符本身
    int weight;//存放权值
    Node1 left;
    Node1 right;

    public Node1(Byte data, int weight) {
        this.data = data;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Node1{" +
                "data=" + data +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int compareTo(Node1 o) {
        return this.weight-o.weight;
    }

    public  void preOrder(){
        System.out.println(this);
        if (this.left!=null){
            this.left.preOrder();
        }
        if (this.right!=null){
            this.right.preOrder();
        }
    }
}
