package com.solvd.navigator;

import com.solvd.navigator.algo.Dijkstra;
import com.solvd.navigator.algo.Graph;
import com.solvd.navigator.connection.ConnectionPool;
import com.solvd.navigator.dao.*;
import com.solvd.navigator.dao.impl.mybatis.*;
import com.solvd.navigator.dao.impl.mybatis.DriverLicenseDAO;
import com.solvd.navigator.model.*;
import com.solvd.navigator.service.ServiceTest;
import com.solvd.navigator.service.imple.LocationService;
import com.solvd.navigator.service.imple.RouteService;
import org.apache.logging.log4j.Logger;

import java.util.Dictionary;
import java.util.Random;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class App {
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(App.class);
    private static final Random random = new Random();

    public static void main(String[] args) {

        getDirectionsUsingService();
        //getDirections();

        //ServiceTest.addCompleteDataToDatabase();

        //ServiceTest.RouteTest();
        //ServiceTest.LocationTest();

//        DaoTest.DLTest();
//        DaoTest.TransportationTypeTest();
//        DaoTest.PersonDriverTest();
//        DaoTest.TransportationTest();
//        DaoTest.LocationTest();
//        DaoTest.RouteTest();
//        DaoTest.ReviewTest();
//        DaoTest.PersonTest();


        /*  Uncomment test below and run  */

//        myBatisLocationTest();
//        myBatisDriverLicenseTest();
//        myBatisTransportationTypeTest();
//        myBatisReviewTest();
//        myBatisTransportationTest(); //bug
//        myBatisPersonDriverTest();
//        myBatisPersonTest();
//        myBatisRouteTest();

    }

    public static void getDirectionsUsingService(){

        Graph graph = new Graph(true, true);
        LocationService locationService = new LocationService();
        RouteService routeService = new RouteService();

        Location SanFrancisco = graph.addLocation(locationService.getById(2L));
        Location LosVegas = graph.addLocation(locationService.getById(4L));
        Location Chicago = graph.addLocation(locationService.getById(1L));
        Location Houston = graph.addLocation(locationService.getById(3L));
        Location Baltimore = graph.addLocation(locationService.getById(6L));
        Location SanAntonio = graph.addLocation(locationService.getById(5L));


        graph.addRoute(routeService.getById(1L));
        graph.addRoute(routeService.getById(2L));
        graph.addRoute(routeService.getById(3L));
        graph.addRoute(routeService.getById(4L));
        graph.addRoute(routeService.getById(5L));
        graph.addRoute(routeService.getById(6L));
        graph.addRoute(routeService.getById(7L));


        Location startingLocation = Chicago;
        Location endingLocation = Baltimore;

        Dictionary[] result = Dijkstra.findAllShortestPath(graph, startingLocation);
        Dictionary<String, Integer> duration = result[0];
        Dictionary<String, Location> AllLocations = result[1];


      Integer durationToDestination = duration.get(endingLocation.getName());
      logger.info("Shortest duration from " + startingLocation.getName() +" to "+ endingLocation.getName() +": " + durationToDestination);
      logger.info("\nPath: ");

        Dijkstra.printPathWithTransportation(AllLocations, endingLocation);
        Result result1 = Dijkstra.result(AllLocations, endingLocation);
        logger.info("\nResult: "+ result1.toString());


    }


    public static void getDirections(){

    Graph graph = new Graph(true, true);

    Location LA = graph.addLocation("Los Angeles");
    Location LV = graph.addLocation("Los Vegas");
    Location NY = graph.addLocation("New York");
    Location SF = graph.addLocation("San Fransisco");
    Location Bro = graph.addLocation("Brooklyn");
    Location Ho = graph.addLocation("Hobart");
    Location Mi = graph.addLocation("Miami");
    Location Sea = graph.addLocation("Seattle");

    Transportation bus = new Transportation();
    bus.getTransportationType().setType("Bus");
    Transportation train = new Transportation();
    train.getTransportationType().setType("Train");

        graph.addRoute(LA,LV,3,bus);
        graph.addRoute(LA,SF,8,train);
        graph.addRoute(SF,LV,7,train);
        graph.addRoute(LV,NY,5,bus);
        graph.addRoute(NY,Bro,3,bus);
        graph.addRoute(Bro,Ho,8,train);
        graph.addRoute(Ho,Mi,7,train);
        graph.addRoute(Mi,Sea,5,train);

        graph.addRoute(LA,Bro,44,train);
        graph.addRoute(LA,Mi,7,train);
        graph.addRoute(Mi,Bro,5,bus);


    Location startingLocation = LA;
    Location endingLocation = Bro;

    Dictionary[] result = Dijkstra.findAllShortestPath(graph, startingLocation);
    Dictionary<String, Integer> duration = result[0];
    Dictionary<String, Location> AllLocations = result[1];

        Integer durationToDestination = duration.get(endingLocation.getName());
        logger.info("Shortest duration from " + startingLocation.getName() +" to "+ endingLocation.getName() +": " + durationToDestination);
        logger.info("\nPath: ");

        Dijkstra.printPathWithTransportation(AllLocations, endingLocation);
        Result result1 = Dijkstra.result(AllLocations, endingLocation);
        logger.info("\nResult: "+ result1.toString());


    }

    private static void connectionPoolTest(){
        // Create an instance of ConnectionPool with a desired pool size
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        try {
            // Acquire a connection from the pool
            Connection connection = connectionPool.getConnection();

            // Ensure the connection is not null
            if (connection != null) {
                logger.info("Connection acquired successfully!\n");

                // Release the connection back to the pool
                connectionPool.releaseConnection(connection);
                logger.info("Connection released successfully!");
            } else {
                logger.info("Failed to acquire connection from the pool.");
            }
        } catch (InterruptedException | SQLException | IOException e) {
            logger.error("Failed to acquire/release connection from the pool.", e);
        }
    }

    private static void myBatisDriverLicenseTest(){

        IDriverLicenseDAO driverLicenseDAO = new DriverLicenseDAO();

        DriverLicense driverLicense = createDriverLicense();
        DriverLicense driverLicense2 = createDriverLicense();

        try {
            driverLicenseDAO.insert(driverLicense);
            logger.info(driverLicenseDAO.getById(driverLicense.getId()).toString() + "\n");

            driverLicense.setNumber(Math.abs(random.nextInt()));
            driverLicenseDAO.update(driverLicense);
            logger.info(driverLicenseDAO.getById(driverLicense.getId()).toString() + "\n");

            driverLicenseDAO.insert(driverLicense2);
            logger.info(driverLicenseDAO.getAll().toString() + "\n");
        } catch (Exception e) {
            logger.error(e);
        } finally {
            driverLicenseDAO.delete(driverLicense.getId());
            driverLicenseDAO.delete(driverLicense2.getId());
        }
    }

    private static void myBatisTransportationTypeTest(){
        ITransportationTypeDAO transportationTypeDAO = new TransportationTypeDAO();

        TransportationType transportationType = createTransportationType("Bus");
        TransportationType transportationType2 = createTransportationType("Plane");

        try {
            transportationTypeDAO.insert(transportationType);
            logger.info(transportationTypeDAO.getById(transportationType.getId()).toString() + "\n");

            transportationType.setType("Train");
            transportationTypeDAO.update(transportationType);
            logger.info(transportationTypeDAO.getById(transportationType.getId()).toString() + "\n");

            transportationTypeDAO.insert(transportationType2);
            logger.info(transportationTypeDAO.getAll().toString() + "\n");
        } catch (Exception e) {
            logger.error(e);
        } finally {
            transportationTypeDAO.delete(transportationType.getId());
            transportationTypeDAO.delete(transportationType2.getId());
        }
    }

    private static void myBatisReviewTest(){
        IReviewDAO reviewDAO = new ReviewDAO();
        ILocationDAO locationDAO = new LocationDAO();

        Location location = createLocation("Los Angeles", new Coordinate(34.0522, -118.2437));

        Review review = createReview("content from review", location);
        Review review2 = createReview("content from review2", location);

        try {
            locationDAO.insert(location);
            reviewDAO.insert(review);
            logger.info(reviewDAO.getById(review.getId()).toString() + "\n");

            review.setContent("updated content from review");
            reviewDAO.update(review);
            logger.info(reviewDAO.getById(review.getId()).toString() + "\n");

            reviewDAO.insert(review2);
            logger.info(reviewDAO.getAll().toString() + "\n");
        } catch (Exception e) {
            logger.error(e);
        } finally {
            reviewDAO.delete(review.getId());
            reviewDAO.delete(review2.getId());
            locationDAO.delete(location.getId());
        }
    }

    private static void myBatisPersonTest(){
        //Test PersonDAO without DriverLicense
        IPersonDAO personDAO = new PersonDAO();

        Person person = createPerson("John");
        Person person2 = createPerson("Jack");

        try{
            personDAO.insert(person);
            logger.info(personDAO.getById(person.getId()).toString() + "\n");

            person.setName("Bob");
            personDAO.update(person);
            logger.info(personDAO.getById(person.getId()).toString() + "\n");

            personDAO.insert(person2);
            logger.info(personDAO.getAll().toString() + "\n");
        }catch (Exception e){
            logger.error(e);
        }
        finally {
            personDAO.delete(person.getId());
            personDAO.delete(person2.getId());
        }
    }

    private static void myBatisPersonDriverTest(){
        // Test PersonDAO with DriverLicense
        IPersonDAO driverDAO = new PersonDAO();
        IDriverLicenseDAO driverLicenseDAO = new DriverLicenseDAO();

        DriverLicense driverLicense = createDriverLicense();
        DriverLicense driverLicense2 = createDriverLicense();

        Person driver = createDriver("John", driverLicense);
        Person driver2 = createDriver("Jack", driverLicense2);

        try{
            driverLicenseDAO.insert(driverLicense);
            driverLicenseDAO.insert(driverLicense2);

            driverDAO.insertDriver(driver);
            logger.info(driverDAO.getDriverById(driver.getId()).toString() + "\n");

            driver.setName("Bob");
            driverDAO.updateDriver(driver);
            logger.info(driverDAO.getDriverById(driver.getId()).toString() + "\n");

            driverDAO.insertDriver(driver2);
            logger.info(driverDAO.getAllDrivers().toString() + "\n");
        }catch (Exception e){
            logger.error(e);
        }finally {
            driverDAO.delete(driver.getId());
            driverDAO.delete(driver2.getId());
            driverLicenseDAO.delete(driverLicense.getId());
            driverLicenseDAO.delete(driverLicense2.getId());
        }
    }

    private static void myBatisLocationTest() {
        // Test LocationDAO, route list is initialized in service layer
        ILocationDAO locationDAO = new LocationDAO();
        Location location1 = createLocation("New York", new Coordinate(40.7128, 74.0060));
        Location location2 = createLocation("Los Angeles", new Coordinate(34.0522, 118.2437));

        //locationDAO.insert(location1);

        try {
            locationDAO.insert(location1);
            logger.info(locationDAO.getById(location1.getId()).toString() + "\n");

            location1.setName("Chicago");
            locationDAO.update(location1);
            logger.info(locationDAO.getById(location1.getId()).toString() + "\n");

            locationDAO.insert(location2);
            logger.info(locationDAO.getAll().toString() + "\n");
        }catch (Exception e){
            logger.error(e);
        }finally {
            locationDAO.delete(location1.getId());
            locationDAO.delete(location2.getId());
        }
    }

    private static void myBatisTransportationTest(){
        // Test TransportationDAO
        ITransportationTypeDAO transportationTypeDAO = new TransportationTypeDAO();
        ITransportationDAO transportationDAO = new TransportationDAO();
        IDriverLicenseDAO driverLicenseDAO = new DriverLicenseDAO();
        IPersonDAO driverDAO = new PersonDAO();

        DriverLicense driverLicense = createDriverLicense();
        DriverLicense driverLicense2 = createDriverLicense();

        Person driver = createDriver("John", driverLicense);
        Person driver2 = createDriver("Jack", driverLicense2);

        TransportationType transportationType = createTransportationType("Bus");
        TransportationType transportationType2 = createTransportationType("Plane");

        Transportation transportation1 = createTransportation(driver, transportationType);
        Transportation transportation2 = createTransportation(driver2, transportationType2);

        try {
            transportationTypeDAO.insert(transportationType);
            transportationTypeDAO.insert(transportationType2);
            driverLicenseDAO.insert(driverLicense);
            driverLicenseDAO.insert(driverLicense2);
            driverDAO.insert(driver);
            driverDAO.insert(driver2);

            transportationDAO.insert(transportation1);
            logger.info(transportationDAO.getById(transportation1.getId()).toString() + "\n");

            transportation1.setVehicleNumber(102);
            transportationDAO.update(transportation1);
            logger.info(transportationDAO.getById(transportation1.getId()).toString() + "\n");

            transportationDAO.insert(transportation2);
            logger.info(transportationDAO.getAll().toString() + "\n");
        }catch (Exception e){
            logger.error(e);
        }finally {
            transportationDAO.delete(transportation1.getId());
            transportationDAO.delete(transportation2.getId());
            driverDAO.delete(driver.getId());
            driverDAO.delete(driver2.getId());
            driverLicenseDAO.delete(driverLicense.getId());
            driverLicenseDAO.delete(driverLicense2.getId());
            transportationTypeDAO.delete(transportationType.getId());
            transportationTypeDAO.delete(transportationType2.getId());
        }
    }

    private static void myBatisRouteTest(){
        IDriverLicenseDAO driverLicenseDAO = new DriverLicenseDAO();
        DriverLicense driverLicense = createDriverLicense();
        DriverLicense driverLicense2 = createDriverLicense();

        ITransportationTypeDAO transportationTypeDAO = new TransportationTypeDAO();
        TransportationType transportationType = createTransportationType("Bus");
        TransportationType transportationType2 = createTransportationType("Plane");

        ILocationDAO locationDAO = new LocationDAO();
        Location locationA = createLocation("Chicago", new Coordinate(41.8781, 87.6298));
        Location locationB = createLocation("San Francisco", new Coordinate(37.7749, 122.4194));
        Location locationC = createLocation("Houston", new Coordinate(29.7604, 95.3698));

        IPersonDAO personDAO = new PersonDAO();
        Person driver = createDriver("John", driverLicense);
        Person driver2 = createDriver("Jack", driverLicense2);

        ITransportationDAO transportationDAO = new TransportationDAO();
        Transportation transportation = createTransportation(driver, transportationType);
        Transportation transportation2 = createTransportation(driver2, transportationType2);

        IRouteDAO routeDAO = new RouteDAO();
        Route route = createRoute(locationA, locationB, transportation,100, 1000);
        Route route2 = createRoute(locationB, locationA, transportation2,100, 1500);

        try{
            driverLicenseDAO.insert(driverLicense);
            driverLicenseDAO.insert(driverLicense2);
            transportationTypeDAO.insert(transportationType);
            transportationTypeDAO.insert(transportationType2);
            locationDAO.insert(locationA);
            locationDAO.insert(locationB);
            locationDAO.insert(locationC);
            personDAO.insertDriver(driver);
            personDAO.insertDriver(driver2);
            transportationDAO.insert(transportation);
            transportationDAO.insert(transportation2);

            routeDAO.insert(route);
            logger.info(routeDAO.getById(route.getId()) + "\n");

            route.setLocationA(locationC);
            routeDAO.update(route);
            logger.info(routeDAO.getById(route.getId()) + "\n");

            routeDAO.insert(route2);
            logger.info(routeDAO.getAll() + "\n");
        }catch (Exception e) {
            logger.error(e);
        }finally {
            routeDAO.delete(route.getId());
            routeDAO.delete(route2.getId());
            transportationDAO.delete(transportation.getId());
            transportationDAO.delete(transportation2.getId());
            personDAO.delete(driver.getId());
            personDAO.delete(driver2.getId());
            locationDAO.delete(locationA.getId());
            locationDAO.delete(locationB.getId());
            locationDAO.delete(locationC.getId());
            transportationTypeDAO.delete(transportationType.getId());
            transportationTypeDAO.delete(transportationType2.getId());
            driverLicenseDAO.delete(driverLicense.getId());
            driverLicenseDAO.delete(driverLicense2.getId());
        }
    }

//    // Helpers for creating model objects
    private static DriverLicense createDriverLicense(){
        long id = Math.abs(random.nextLong());
        int number = Math.abs(random.nextInt());
        DriverLicense driverLicense = new DriverLicense();
        driverLicense.setId(id);
        driverLicense.setNumber(number);
        return driverLicense;
    }

    private static Person createDriver(String name, DriverLicense driverLicense){
        long id = Math.abs(random.nextLong());
        Person driver = new Person();
        driver.setId(id);
        driver.setName(name);
        driver.setDriverLicense(driverLicense);
        return driver;
    }

    private static TransportationType createTransportationType(String type){
        long id = Math.abs(random.nextLong());
        TransportationType transportationType = new TransportationType();
        transportationType.setId(id);
        transportationType.setType(type);
        return transportationType;
    }

    private static Location createLocation(String name, Coordinate coordinate){
        long id = Math.abs(random.nextLong());
        Location location = new Location();
        location.setId(id);
        location.setName(name);
        location.setCoordinate(coordinate);
        return location;
    }

    private static Review createReview(String content, Location location){
        long id = Math.abs(random.nextLong());
        Review review = new Review();
        review.setId(id);
        review.setContent(content);
        review.setLocation(location);
        return review;
    }

    private static Person createPerson(String name){
        long id = Math.abs(random.nextLong());
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        return person;
    }

    private static Transportation createTransportation(Person driver, TransportationType transportationType){
        long id = Math.abs(random.nextLong());
        Transportation transportation = new Transportation();
        transportation.setId(id);
        transportation.setCost(100);
        transportation.setVehicleNumber(101);
        transportation.setDriver(driver);
        transportation.setTransportationType(transportationType);
        return transportation;
    }

    private static Route createRoute(Location locationA, Location locationB, Transportation transportation,  int duration, int distance){
        long id = Math.abs(random.nextLong());
        Route route = new Route();
        route.setId(id);
        route.setLocationA(locationA);
        route.setLocationB(locationB);
        route.setTransportation(transportation);
        route.setDuration(duration);
        route.setDistance(distance);
        return route;
    }
}
