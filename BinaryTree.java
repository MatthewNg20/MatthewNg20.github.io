public class BinaryTree{

    class Node {

        final int key;
        Node leftChild, rightChild;

        Node(int key){
            this.key = key;

        }

        public String toString(){
            return "" + key;
        }

        public void print() {
            print("", this, false);
        }

        private void print(String prefix, Node n, boolean isLeft) {
            if (n != null){
                System.out.println(prefix + (isLeft ? "|--" : "\\-- ") + n.key);
                print(prefix + (isLeft ? "|  " : "    "), n.leftChild,true);
                print(prefix + (isLeft ? "|  " : "    "), n.rightChild,false);
            }
        }
    }

    private Node addRecursive (Node current, int value){
        if (current == null) {
            return new Node(value);
        }

        if (value < current.key) {
            current.leftChild = addRecursive(current.leftChild, value);
        }
        else if (value > current.key){
            current.rightChild = addRecursive(current.rightChild, value);
        }
        else {
            return current;
        }
        return current;
    }

    Node root;

    public void insert(int key){
        root = addRecursive(root, key);
    }

    public void inOrderTraversal(Node focusNode){

        if(focusNode != null){
            inOrderTraversal(focusNode.leftChild);
            System.out.print(focusNode + " ");
            inOrderTraversal(focusNode.rightChild);
        }
    }

    public void preOrderTraversal(Node focusNode){

        if(focusNode != null){
            System.out.print(focusNode + " ");
            preOrderTraversal(focusNode.leftChild);
            preOrderTraversal(focusNode.rightChild);
        }
    }

    public void postOrderTraversal(Node focusNode){

        if(focusNode != null){
            postOrderTraversal(focusNode.leftChild);
            postOrderTraversal(focusNode.rightChild);
            System.out.print(focusNode + " ");
        }
    }

    public Node searchNode(int key){

        Node focusNode = root;

        while (focusNode.key != key){

            if(key < focusNode.key){
                focusNode = focusNode.leftChild;
            }
            else {
                focusNode = focusNode.rightChild;
            }

            if(focusNode == null){
                return null;
            }
        }
        return focusNode;
    }

    public boolean delete(int key){

        Node focusNode = root;
        Node parent = root;

        boolean isItALeftChild = true;

        while(focusNode.key != key){
            parent = focusNode;

            if(key < focusNode.key){
                isItALeftChild = true;
                focusNode = focusNode.leftChild;
            }
            else {
                isItALeftChild = false;
                focusNode = focusNode.rightChild;
            }

            if(focusNode == null){
                return false;
            }
        }
        if (focusNode.leftChild == null && focusNode.rightChild == null){
            if(focusNode == root){
                root = null;
            }
            else if(isItALeftChild){
                parent.leftChild = null;
            }
            else{
                parent.rightChild = null;
            }
        }
        else if(focusNode.rightChild ==null){
            if(focusNode == root) {
                root = focusNode.leftChild;
            }
            else if(isItALeftChild){
                parent.leftChild = focusNode.leftChild;
            }
            else{
                parent.rightChild = focusNode.leftChild;
            }
        }

        else if(focusNode.leftChild == null){
            if(focusNode == root){
                root = focusNode.rightChild;
            }
            else if(isItALeftChild){
                parent.leftChild = focusNode.rightChild;
            }
            else{
                parent.rightChild = focusNode.leftChild;
            }
        }

        else {
            Node replacement = getReplacementNode(focusNode);

            if(focusNode == root){
                root = replacement;
            }

            else if(isItALeftChild){
                parent.leftChild = replacement;
            }
            else{
                parent.rightChild = replacement;
            }
            replacement.leftChild = focusNode.leftChild;
        }
        return true;
    }

    public Node getReplacementNode(Node replacedNode){

        Node replacementParent = replacedNode;
        Node replacement = replacedNode;

        Node focusNode = replacedNode.rightChild;

        while(focusNode != null){
            replacementParent = replacement;
            replacement = focusNode;
            focusNode = focusNode.leftChild;
        }

        if(replacement != replacedNode.rightChild){
            replacementParent.leftChild = replacement.rightChild;
            replacement.rightChild = replacedNode.rightChild;
        }
        return replacement;
    }



    public static void main(String[] args) {

        BinaryTree a = new BinaryTree();

        a.insert(50);
        a.insert(25);
        a.insert(65);
        a.insert(90);
        a.insert(85);
        a.insert(10);
        a.insert(18);

        System.out.println();
        System.out.println("In-Order Traversal");
        a.inOrderTraversal(a.root);

        System.out.println();
        System.out.println("Pre-Order Traversal");
        a.preOrderTraversal(a.root);

        System.out.println();
        System.out.println("Post-Order Traversal");
        a.postOrderTraversal(a.root);

        System.out.println();
        System.out.println("Searching for key:");
        System.out.println(a.searchNode(30));

        System.out.println();
        System.out.println("Removing key:");
        a.delete(25);

        System.out.println();
        a.preOrderTraversal(a.root);
        System.out.println();

        System.out.println("NOTE:");
        System.out.print("\\-- means RIGHT subtree");
        System.out.println("|-- means LEFT subtree");
        System.out.println();
        a.root.print();
    }
}
