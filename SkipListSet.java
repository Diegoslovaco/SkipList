/*
*   Diego Cruces
*   Programming Project
*   COP3503C
*
*   References:
*       - Java Documentation
*       - Dr. Matthew Gerber
*
* */




import java.lang.reflect.Array;
import java.util.*;


/*
*   This Object represent a Skiplist that has properties of a sorted set
*   No duplicate element, and all elements are inserted from smallest
*   to greatest
*
* */
public class SkipListSet <T extends Comparable<T>> implements SortedSet<T> {


    private int size = 0;           //keeps track of how many elements are on the SkipList
    private int currentHeight = 0;  //Keeps track of the SkipList Height
    SkipListSetItem heads;          //This is always the starting point, aka the Head


    /*
    *   Constructor that returns an empty SkipList,
    *
    * */
    public SkipListSet(){
        setHeads();
        size = 0;
        currentHeight = 0;
        heads = new SkipListSetItem();
    }


    /*
    *   Constructor that returns a populated SkipList Set
    *   @param: A generic Collection
    *
    * */
    public SkipListSet(Collection<? extends T> c){
        setHeads();
        size = 0;
        currentHeight = 0;
        heads = new SkipListSetItem();
        addAll(c);
    }


    /*
    *   We set our Heads tower depending on our current height,
    *   By default the point to null
    *
    * */
    private void setHeads(){
        SkipListSetItem temp = heads;
        for(int i = 0; i < currentHeight -1; i++){
            SkipListSetItem newItem = new SkipListSetItem(); //For now a simple link listed is used
            temp.up = newItem;
            newItem.down = temp;
            temp = newItem;
        }
    }


    /*
    *   This method is used to set the Height of a new Element
    *   By default all element have height 1
    *   Each node always have 50% change of increasing its Height
    *   However no node can have Height greater than current height
    *
    *   @return     int height
    *
    * */
    private int setHeight(){
        Random rand = new Random();

        int height = 1;
        boolean val = rand.nextInt(2)==0;

        while (val && height < currentHeight){
            height++;
            val = rand.nextInt(2)==0;
        }

        return height;
    }



    /*
    *   We modify our current skipList Height based on how many elements
    *   there are.
    *
    *   Specifically our current Height is the log base 2 of how many elements
    *   there are
    *
    * */
    public void setCurrentHeight(){
        int temp = log2(size);  // get new Height


        if(temp > currentHeight){ //New height is greater than current height
            temp = temp - currentHeight; // Determine the difference between Heights

            //Go all the way up!
            SkipListSetItem newItem = heads;
            while (newItem != null){
                if(newItem.up == null){
                    break;
                }
                newItem = newItem.up;

            }

            //Add new the new heads!
            for(int i = 0; i < temp; i ++){
                SkipListSetItem newHeight = new SkipListSetItem();
                newItem.up = newHeight;
                newHeight.down = newItem;
                newItem = newItem.up;
            }



        }else{ //Current height is less that currentHeight

            temp = currentHeight - temp; //Determine by how much

            //Go all the way up!
            SkipListSetItem newItem = heads;
            while (newItem.up != null){
                newItem = newItem.up;
            }

            //Dereference!
            for (int i = 0; i < temp-1; i ++){
                newItem = newItem.down;
                newItem.up = null;
            }
        }

        //Lastly we set our currentHeight
        currentHeight = log2(size);
    }



    /*
    *   We set the hashCode for our whole SkipList set
    *   We do so by adding the hashCode of all our elements
    *   and their towers
    *
    *
    *   @return     int hashCode
    * */
    public int hashCode(){

        int hasCode = 0;
        SkipListSetItem temp = heads.right; //First Element
        SkipListSetItem stairs = heads.right; //We use this to go up!

        while (temp != null){ //if there are elements
            while(stairs != null){ //Repeat!
                hasCode += stairs.payload.hashCode(); //Add elements hash
                stairs = stairs.up;//Go up!
            }
            temp = temp.right; //Next one
            stairs = temp;
        }

        return hasCode; //return our hash
    }

    /*
    *   Determine if 2 SkipLists Sets are equal!
    *
    *   @return     boolean equals
    *
    * */
    public boolean equals(SkipListSet set){

        if(set.size() != size){ //If the 2 sets are not even the same size we don't even bother!
            return false;
        }

        //Now lets compare elements
        //We can assume they are in order since we are dealing with Sorted sets!
        SkipListSetItem thisSet = heads.right;
        SkipListSetItem otherSet = set.heads.right;

        for(int i = 0; i < size; i++){
            if(thisSet.payload.equals(otherSet.payload) == false){
                return false; //if no 2 elements are equal at the same position we return false
            }
        }
        return true;//else they are the same!
    }


    /*
    *   we reset the heights of all our elements
    * */
    public void reBalance(){

        SkipListSetItem temp = heads;
        SkipListSetItem temp2;

        temp = temp.right; //First element!
        while (temp != null && temp.right != null){ //while there are elements to rebalances

            temp2 = temp; // we save the base of current elements
            int newHeight = setHeight(); // get the element new height
            int currentHeight = 1; //We start at height 1

            while (temp.up != null){ //Go all the way up!
                if(temp.up == null){
                    break;
                }
                temp = temp.up;
                currentHeight++;
            }
            while (newHeight != currentHeight){ //If the 2 Heights are not the same then. . .

                if(currentHeight > newHeight){ //New heights is less, then we need to break links :(
                    if(temp == null){ // we stop once we reach null
                        break;
                    }

                    //break links!
                    temp.left.right = temp.right;

                    if(temp.right == null){
                        temp.right = null;
                    }else{
                        temp.right.left = temp.left;
                    }

                    //Go down and decrease current height
                    temp = temp.down;
                    currentHeight--;
                }

                if(currentHeight < newHeight){ //If current height is greater than, then . . .
                    //Create new Item to add and added!
                    SkipListSetItem newItem = new SkipListSetItem(temp.payload);
                    temp.up = newItem;
                    newItem.down = temp;
                    temp = temp.up;


                    //Set its left and right links!
                    SkipListSetItem temp3 = temp;
                    while(temp3.left != null){ // We go left!
                        temp3 = temp3.left;
                        if(temp3.up != null){ // and we go one up!
                            //if we find one up then move up and set links!
                            temp3 = temp3.up;
                            temp.right = temp3.right;
                            temp.left = temp3;
                            temp3.right = temp;
                            break;
                        }
                    } // repeat!
                    newHeight--; // decrease :)
                }
            }
            temp = temp2.right; // next element!
        }
        

    }


    /*
    *   Method that I used to test SkipList add/remove/contains methods
    *   Not recommended to use for large quantities of elements
    * */
    public void printSkipList(){ //Prints link list
        SkipListSetItem temp = heads;
        System.out.println("Printing List");
        while (temp.right != null){
            temp = temp.right;
            System.out.println(temp.payload);

        }


    }


    /*
    *   returns First element on the list
    * */
    @Override
    public T first() {
        T first = heads.right.payload;
        return first;
    }

    /*
    *   returns last Element on the list
    * */
    @Override
    public T last() {
        SkipListSetItem temp = heads;

        while(temp.right != null){
            temp = temp.right;
        }
        T last = temp.payload;
        return last;
    }


    /*
    *   Return the size of the list
    * */
    @Override
    public int size() {
        return size;
    }


    /*
    *   Tells whether the list is empty or not
    * */
    @Override
    public boolean isEmpty() {
        SkipListSetItem temp = heads;
        boolean isEmpty = true;
        if(temp.right != null){
            isEmpty = false;
        }
        return isEmpty;
    }


    /*
    *  Returns true if the list has certain element
    *  otherwise false
    *
    *   @param  Object o
    *
    * */
    @Override
    @SuppressWarnings({"rawtypes"})
    public boolean contains(Object o) {
        SkipListSetItem temp = heads;//start at head
        while (temp.up != null){
            if(temp.up == null){
                break;
            }
            temp = temp.up; // move all the way up!
        }

        boolean contains = false;
        while (temp != null){ //while our reference is not null

            //Move all the way right until either next is null
            //Or next is greater than our searched elements
            while(temp.right != null && temp.right.payload.compareTo((T)o) <= -1){
                temp = temp.right;
            }


            if(temp.right != null){//if next element is not null
                //if next element is the same, return true!
                if(temp.right.payload.compareTo((T)o) == 0 || o.equals(temp.right.payload)){
                    return true;
                }

                //If next element is greater than, go down and repeat!
                if( temp.right.payload.compareTo((T)o) >= 1){
                    temp = temp.down;
                    continue;
                }
            }

            //If next element is null, simply go down
            if(temp.right == null){
                temp = temp.down;
                continue;
            }

            //this is just a safe net, but still basically just go down
            if(temp != null && temp.down != null){
                temp = temp.down;
                continue;
            }else{
                return false;
            }
        }
        return contains;
    }


    /*
    *   Return our SkipList iterator!
    * */
    @Override
    public java.util.Iterator<T> iterator() {//not yet implemented
        SkipListSetIterator Iterator = new SkipListSetIterator();
        return Iterator;
    }


    /*
    *   Return an array of objects
    * */
    @Override
    public Object[] toArray() { // not yet implemented

        SkipListSetItem temp = heads.right;
        Object[] myArray =  new Object[size];




        for(int i = 0; i < size; i++){
            myArray[i] = temp.payload;
            temp = temp.right;
            if(temp == null){
                break;
            }
        }
        return myArray;
    }


    /*
    *   Given an array we populate it with our List elements
    *
    * */
    @Override
    public <T1> T1[] toArray(T1[] a) {

        SkipListSetItem temp = heads.right;
        if (a.length < size) {
            a = (T1[]) Array.newInstance(a.getClass().getComponentType(), size);



        } else if (a.length > size) {
            a[size] = null;
        }

        for(int i = 0; i < size; i++){
            a[i] = (T1) temp.payload;
            temp = temp.right;
        }
        return a;
    }


    /*
    *   We add element t to our List
    * */
    @Override
    @SuppressWarnings({"rawtypes"})
    public boolean add(T t) {



        int newNodeheight = setHeight();    //gets height of new element
        SkipListSetItem temp = heads;       //Start at heads!
        SkipListSetItem newItem = new SkipListSetItem(t); //  create new item wrapper with payload = t
        int currentHeight = 1; //We always start at Height 1

        //Go all the way up!
        while (temp.up != null){
            if(temp.up == null){
                break;
            }
            temp = temp.up;
            currentHeight++; //Keep track of where we are
        }

        while (temp != null){ //while our element is not null

            //go all the way right until either next is not null or next element is greater than
            while(temp.right != null && temp.right.payload.compareTo(t) <= -1){
                temp = temp.right;
            }

            //If we are not at the same level simply go down and repeat
            if(currentHeight != newNodeheight){
                temp = temp.down;
                currentHeight--; // decrease level
            }else{

                //else Set links, be careful with null
                newItem.left = temp;
                if(temp.right == null){
                    newItem.right = null;
                }else{
                    newItem.right = temp.right;
                }
                temp.right = newItem;

                if(temp.down == null){
                    newItem.down = null;

                }else{
                    SkipListSetItem down = new SkipListSetItem(t);
                    newItem.down = down;
                    newItem = newItem.down;
                }

                temp = temp.down; // go down
            }



        }




        size++; //Increase height
        setCurrentHeight(); //set current height
        return true;
    }


    /*
    *   Returns the log base 2 of any passed number
    *
    *   @param  int a
    *   @return int result
    * */
    public int log2(int a){
        int result = (int)Math.floor(Math.log(a) / Math.log(2));
        return result;
    }

    /*
    *   Remove an item using the iterator
    * */
    public boolean removeUsingIterator(Object o) {
        return  remove(o);
    }


    /*
    *   Remove an Object o from our SkipList set
    *
    * */
    @Override
    public boolean remove(Object o) {

        SkipListSetItem temp = heads;//start at head

        //go all the way up!
        while (temp.up != null){
            if(temp.up == null){
                break;
            }
            temp = temp.up;
        }

        boolean remove = false;

        while (!remove){// while we don't find our element for deletion keep searching!

            // if right is null
            if(temp.right == null){
                if(temp.down == null){ //check if we are on a corner
                    break;
                }
                temp = temp.down; // go down

            }else{
                //If our next element is equal we find it!
                if(temp.right.payload.compareTo((T)o) == 0 || o.equals(temp.right.payload)){ // if payloads are the same return true
                    remove = true;
                    temp = temp.right;
                    break;

                }


                //If our next element is next move right
                if(temp.right.payload.compareTo((T)o) <= -1){
                    temp = temp.right;
                    //System.out.println("Current payload is less,moving right");
                    continue;
                }

                //if our next element is greater!
                if(temp.right.payload.compareTo((T)o) >= 1){
                    if(temp.down == null){ //if down is null we could find it
                        remove = false;
                        break;
                    }
                    //else fo down
                    temp = temp.down;
                    continue;
                }

                //safe net to avoid any infinite loops
                temp = temp.right;

            }

        }

        //if element to remove was find dereference the whole tower!
        if(remove){
            size--; //decrease list size

            //Start dereferencing!
            //Notice we are at the top of the tower of the element to be removed
            //So we only need to go down
            while (temp != null){

                if(temp.right != null){
                    if(temp.left != null){
                        temp.left.right = null;
                    }
                    temp.right.left = temp.left;
                }
                temp = temp.down;
            }

        }

        setCurrentHeight();//Set our new List height
        return remove;
    }


    /*
    *   Returns whether of not our Skip List contains
    *   all the elements of the passed collection
    *
    *   @param  Collection<?> c
    *   @return boolean
    * */
    @Override
    public boolean containsAll(Collection<?> c) {

        for(Object t: c){
            if(!contains(t)){
                return false;
            }
        }
        return true;
    }


    /*
    *   Adds all elements from a collection to our list
    *
    *   @param  Collection<? extends T> c
    *   @return Boolean
    * */
    @Override
    public boolean addAll(Collection<? extends T> c) {

        for(T t : c){
            if(!contains(t)){
                add(t);
            }

        }
        return true;
    }


    /*
    *   Keeps elements that are only on the collection
    *   and the skip list
    *
    *   @param  Collection<?> c
    * */
    @Override
    public boolean retainAll(Collection<?> c) {

        boolean retainAll = false;
        ArrayList<Object> objs = new ArrayList<>(); // we create an array list of object

        for(Object t: c){
            if(contains(t)){
                objs.add(t); // we only add elements that are ob both c and our skipList
            }
        }

        heads.right = null;  //Basically start an empty skipList

        //Add the objects
        for(int i = 0; i < objs.size(); i++){
            System.out.println("adding: " + objs.get(i));
            add((T) objs.get(i));
            retainAll = true;
        }

        return retainAll;

    }


    /*
    *   Remove all element from our list that are in a collection
    *
    *   @param  Collection<?> c
    *   @return boolean
    * */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removeAll = false;

        for(Object t : c){ // loop trrough elements of the collection
            removeAll = contains(t);//check if element is already on skiplist
            if(removeAll == true){ //if yes delete
                remove(t);
            }
        }
        return removeAll;
    }


    /*
    *   Clear our SkipList
    * */
    @Override
    public void clear() {
        setHeads();
    }


    /*
    *   Class that works as a item wrapper
    *   this class have 4 pointers for easier and more intuitive traversal
    *       left, right, up, & down
    *
    *   We implement comparable to compare the items payloads
    * */
    private class SkipListSetItem implements Comparable<T>{
        T payload;

        SkipListSetItem right, left, up, down;


        //Constructor for an item that receives a payload
        public SkipListSetItem(T payload){
            this.payload = payload;
        }

        //Constructor for an empty item
        public SkipListSetItem() {
            right = null;
            left = null;
            down = null;
            up = null;
        }




        /*
        *   Compare to method that compare items payload
        *
        *   @param  T o
        *   @return int compareTo
        *
        * */
        @Override
        public int compareTo(T o) {

            //Compares Payloads
            int compareTo = 0;
            if(o.compareTo(payload) == 0 || o.equals(payload)){
                compareTo = 0;
            }else if(o.compareTo(payload) <= -1){
                compareTo = -1;
            }else if(o.compareTo(payload) >= 1){
                compareTo = 1;
            }

            return compareTo;

        }
    }


    /*
    *   Iterator class that allow us for easy traversal of our SkipList
    * */
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator <T>{

        SkipListSetItem currentItem;

        //When declareing our iterator we start from heads
        public SkipListSetIterator(){
            currentItem = heads;
        }

        /*
        *   If our current item is not null and the next item is not null then
        *   there is a next item
        * */
        @Override
        public boolean hasNext() {
            if(currentItem != null && currentItem.right != null){
                return true;
            }
            return false;
        }

        /*
        *   We return the payload of next item
        * */
        @Override
        public T next() {
            if(hasNext() == true){
                currentItem = currentItem.right;
                T payload = (T) currentItem.payload;

                return payload;
            }
            return null;
        }

        /*
        *   We remove current item and go to next item
        * */
        @Override
        public void remove() {
            SkipListSetItem temp = currentItem.right;
            removeUsingIterator(currentItem.payload);
            currentItem = temp;
        }
    }




    //Unsoported methods
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }



}
