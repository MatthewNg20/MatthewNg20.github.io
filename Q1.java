import java.util.Arrays;
import java.util.concurrent.*;

//ASSUMPTION: Index out of bounds can happen if the image is too small and too many iterations
class Convolution implements Callable<int[][]> {
    // Initializing variables
    private int[][] kernel = {
            {1,0,1},
            {0,1,0},
            {1,0,1},
    };
    private int[][] inputArray;
    private int startingX;
    private int startingY;
    private int length;

    // Constructor
    Convolution(int[][] inputArray, int startingX, int startingY, int length){
        this.inputArray = inputArray;
        this.startingX = startingX;
        this.startingY = startingY;
        this.length = length;
    }

    //Convolution process
    public int[][] call() throws Exception{
        //This is the output array
        int[][] outputArray = new int[length-2][length-2];

        //This is a temporary array to store the necessary cells for convolution
        //For instance, for the TOP LEFT quadrant, the process array will store the top left section of the input array for convolution purposes
        int[][] processArray = new int[length][length];

        // Add into another array containing the split cells
        // It will have array.length / 2 + 1
        // (Meaning that if the starting image is 8, the expected size of the quadrant is 5, the expected output size is 3)
        int x = 0;
        for(int i = startingX; i < (startingX+length); i ++){
            int y = 0;
            for(int j = startingY; j < (startingY+length); j++){
                processArray[x][y] = inputArray[i][j];
                y += 1;
            }
            x += 1;
        }

        for(int i = 1; i < (length - 1); i++){
            for(int j = 1; j < (length - 1); j++){
                int topLeft = kernel[0][0] * processArray[i-1][j-1];
                int top = kernel[0][1] * processArray[i-1][j];
                int topRight = kernel[0][2] * processArray[i-1][j+1];

                int left = kernel[1][0] * processArray[i][j-1];
                int centre = kernel[1][1] * processArray[i][j];
                int right = kernel[1][2] * processArray[i][j+1];

                int bottomLeft = kernel[2][0] * processArray[i+1][j-1];
                int bottom = kernel[2][1] * processArray[i+1][j];
                int bottomRight = kernel[2][2] * processArray[i+1][j+1];

                int outputValue = topLeft + top + topRight + left + centre + right + bottomLeft + bottom + bottomRight;
                outputArray[i-1][j-1] = outputValue;
            }
        }
        return outputArray;
    }
}

public class Q1 {
    public static void main(String[] args){

        //Manually define the size of the input array or image here (row x column)
        int size = 7;
        //Input array variable
        int[][] inputArray;

        // Manually define the iterations here
        int iterations = 2;

        //Extra measures to account for odd-sized images
        //If odd-sized, then add an extra "fake" row and column so that the image becomes even
        if(size % 2 == 0){
            inputArray = new int[size][size];
        }
        else{
            inputArray = new int[size+1][size+1];
        }

        //Number of rows the input array contains. If the input array is odd-sized, then the number of rows will reflect the length as the actual size + 1 fake row and column
        int numRow = inputArray.length;

        //Arbitrary initialisation of value to be placed inside the array
        int value = 0;

        // Random creation of array; The array always have incrementing values starting from 0
        for(int i = 0; i < numRow; i++){
            for(int j = 0; j < numRow; j++){
                if(size % 2 == 0){
                    inputArray[i][j] = value;
                    value += 1;
                }else{
                    //This is to add an extra layer of buffer to the array. With this line, the "fake" rows and columns should appear as extreme negatives
                    if((i == numRow-1) || j == numRow-1){
                        inputArray[i][j] = -500;
                    }
                    else{
                        inputArray[i][j] = value;
                        value += 1;
                    }
                }
            }
        }

        //Initialisation of executor service. Fixed thread pool size of 4 + 1 is used
        //4 threads for 4 regions + 1 extra for optimization
        ExecutorService executor = Executors.newFixedThreadPool(5);

        //Looping process based on the number of iterations
        //The process will throw exception if iteration count is higher than the maximum possible rounds of convolution applicable to a certain image
        for(int x = 0; x < iterations; x++){

            //The delegation of tasks to their respective threads starts here
            //Length: Length of the REQUIRED array
            Convolution TL = new Convolution(inputArray, 0, 0,((inputArray.length) /2)+1);
            Convolution TR = new Convolution(inputArray, 0,((inputArray.length)/2)-1 ,((inputArray.length) /2)+1);
            Convolution BL = new Convolution(inputArray, ((inputArray.length)/2)-1, 0,((inputArray.length) /2)+1);
            Convolution BR = new Convolution(inputArray, ((inputArray.length)/2)-1, ((inputArray.length)/2)-1,((inputArray.length) /2)+1);

            // Control group; convolution process without splitting the image beforehand
            Convolution CG = new Convolution(inputArray, 0,0, inputArray.length);

            Future<int[][]> futureTL = executor.submit(TL);
            Future<int[][]> futureTR = executor.submit(TR);
            Future<int[][]> futureBL = executor.submit(BL);
            Future<int[][]> futureBR = executor.submit(BR);
            // Control group; convolution process without splitting the image beforehand
            Future<int[][]> futureCG = executor.submit(CG);

            int[][] convTL = new int[0][];
            int[][] convTR = new int[0][];
            int[][] convBL = new int[0][];
            int[][] convBR = new int[0][];
            int[][] convCG = new int[0][];

            try{
                convTL = futureTL.get();
                convTR = futureTR.get();
                convBL = futureBL.get();
                convBR = futureBR.get();
                convCG = futureCG.get();
            }catch(InterruptedException | ExecutionException e){
                e.printStackTrace();
            }

            //Create a new array that stores the freshly convoluted sub-images
            int[][] nextIterationArray = new int[(inputArray.length)-2][(inputArray.length)-2];

            //Add all of the 4 convoluted sub-images into one array
            for(int i = 0; i < ((inputArray.length /2)-1); i++){
                for(int j = 0; j <((inputArray.length /2)-1); j++){
                    //System.out.println(convTL[i][j]);
                    //System.out.println(convTR[i][j]);
                    nextIterationArray[i][j] = convTL[i][j];
                    nextIterationArray[i][j+((inputArray.length /2)-1)] = convTR[i][j];
                    nextIterationArray[i+((inputArray.length /2)-1)][j] = convBL[i][j];
                    nextIterationArray[i+((inputArray.length /2)-1)][j+((inputArray.length /2)-1)] = convBR[i][j];
                }
            }

            //Printing the results
            System.out.println("Note 1: The resultant image is printed row by row.");
            System.out.println("Note 2: If the input image is odd-sized, the last values of each row AND the values of the last row will be WRONG as they are 'fake'. ");
            System.out.println("Resultant image after convolution without multithreading: " + Arrays.deepToString(convCG));
            System.out.println("Resultant image after convolution with multithreading: "+ Arrays.deepToString(nextIterationArray));
            //Use the freshly convoluted image as the new input array and restart the process of convolution according to the number of iterations defined
            inputArray = nextIterationArray;
        }

        //Terminating the threads after the process is complete
        executor.shutdown();
    }
}
