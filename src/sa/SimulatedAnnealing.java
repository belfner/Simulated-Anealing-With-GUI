package sa;



import javax.swing.*;

import java.util.Random;
public class SimulatedAnnealing{
    
    //******************************
    public static long seed = 999569360;
    //******************************
    
    static Random rnd = new Random(seed);
    //rnd.setSeed(50);
    
    public static Tour currentSolution;
    public static Tour best;
    
     // Set initial temp
    public static double temp;
    public static double startTemp = 1000000;
    // Cooling rate
    public static double coolingRate = 0.0000005;
    public static double secondsLeft;
    public static double cyclesLeft;
    
    public static int minimum = 5;
    public static int maximum = 200;
    public static int cityCount = 30;
    
    // Calculate the acceptance probability
    public static double acceptanceProbability(int energy, int newEnergy, double temperature) {
        // If the new solution is better, accept it
        if (newEnergy < energy) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((energy - newEnergy) / temperature);
    }

    public static void setupCities(){
        TourManager.resetCities();
        for(int i = 0; i<cityCount;i++){
            City city = new City(minimum + (int)(rnd.nextDouble() * maximum), minimum + (int)(rnd.nextDouble() * maximum));
            TourManager.addCity(city);
        }
    }
    
    public static void main(String[] args) {
        final Draw draw = new Draw();
        SwingUtilities.invokeLater(new Runnable() {
        @Override
         public void run() {
            //draw = new Draw(); // Let the constructor do the job
            draw.init();
        }
        });
        double startTime = 0;
        double endTime = 0;
        
        //while(true){
            // Create and add our cities
            setupCities();
            int cycles = 0;
            // Initialize intial solution
            currentSolution = new Tour();
            currentSolution.generateIndividual();
            
            System.out.println("Initial solution distance: " + currentSolution.getDistance());
            temp = startTemp;
            // Set as current best
            best = new Tour(currentSolution.getTour());
            
            startTime = System.nanoTime();
            // Loop until system has cooled
            while (temp > 1) {
                
                cyclesLeft = (-Math.log10(temp*1.0000005))/Math.log10(1-coolingRate);

                if(cycles % 100000 == 0){
                    endTime = System.nanoTime();  
                
                    double spmil = (endTime -startTime)/1000000000;

                    secondsLeft = (spmil/cycles)*cyclesLeft;
                }
                // Create new neighbour tour
                Tour newSolution = new Tour(currentSolution.getTour());

                // Get a random positions in the tour
                int tourPos1 = (int) (newSolution.tourSize() * rnd.nextDouble());
                int tourPos2 = (int) (newSolution.tourSize() * rnd.nextDouble());

                // Get the cities at selected positions in the tour
                City citySwap1 = newSolution.getCity(tourPos1);
                City citySwap2 = newSolution.getCity(tourPos2);

                // Swap them
                newSolution.setCity(tourPos2, citySwap1);
                newSolution.setCity(tourPos1, citySwap2);

                // Get energy of solutions
                int currentEnergy = currentSolution.getDistance();
                int neighbourEnergy = newSolution.getDistance();

                // Decide if we should accept the neighbour
                if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > rnd.nextDouble()) {
                    currentSolution = new Tour(newSolution.getTour());
                }

                // Keep track of the best solution found
                if (currentSolution.getDistance() < best.getDistance()) {
                    best = new Tour(currentSolution.getTour());
                   // draw.bestDist = best.getDistance();
                    //draw.repaint();
                }

                // Cool system
                temp *= 1-coolingRate;
                draw.temp = (int) temp;
                draw.cycles = cycles;
                draw.repaint();
                cycles ++;
            }
            secondsLeft = 0.0;
            cyclesLeft = 0.0;
            System.out.println("Final solution distance: " + best.getDistance());
            System.out.println("Tour: " + best);
        }
    //}
}