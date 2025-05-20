
# SoulM - Installation and Running Guide  

SoulM is a dating application that consists of both a frontend and a backend. The frontend is built with React, and the backend is built with Spring Boot. Follow the steps below to set up and run both parts of the project on your computer.

-----------------------------------------
## STEP 1: INSTALL THE REQUIRED PROGRAMS
-----------------------------------------

To run the project, you need to install the following programs on your computer:

1. **Node.js** (https://nodejs.org/)
2. **Git** (https://git-scm.com/)
3. **Maven** (https://maven.apache.org/download.cgi)
4. **Visual Studio Code** or another code editor (https://code.visualstudio.com/)

After installing them, open a terminal and check if Node.js, npm, and Maven are installed by running:

    node -v
    npm -v
    mvn -v

If version numbers appear for all, everything is set up correctly.

-----------------------------------------
## STEP 2: DOWNLOAD THE PROJECT FROM GITHUB
-----------------------------------------

To get the project on your computer, follow these steps:

**Method 1: Download with Git (Recommended)**  
Open a terminal, navigate to the folder where you want to download the project, and run:

    git clone https://github.com/Fatihtkc/Dating-Website.git

Once the download is complete, navigate into the project folder with:

    cd Dating-Website

**Method 2: Download as a ZIP File**  
1. Go to the GitHub page: [SoulM GitHub](https://github.com/Fatihtkc/Dating-Website)  
2. Click the **Code** button and select **Download ZIP**.  
3. Extract the downloaded ZIP file to a folder on your computer.  
4. Open a terminal inside the extracted folder.

-----------------------------------------
## STEP 3: SET UP THE DATABASE
-----------------------------------------

1. You need to import the provided **sql** file into a MySQL database named **soulm_database**.  
2. Use the following command to import the SQL file into your database:

    ```sql
    mysql -u your_username -p soulm_database < path_to_soulm_database.sql
    ```

-----------------------------------------
## STEP 4: UPDATE DATABASE CREDENTIALS IN `application.properties`
-----------------------------------------

In the **backend** folder, navigate to the `src/main/resources` directory and open the **application.properties** file.

1. Update the following lines with your MySQL username and password:

    ```properties
    spring.datasource.username=root
    spring.datasource.password=1234
    ```

    Replace `root` and `1234` with the actual username and password for your MySQL account.

-----------------------------------------
## STEP 5: SET UP THE BACKEND (Spring Boot)
-----------------------------------------

1. Navigate to the **backend** folder:

    ```bash
    cd backend
    ```

2. Run the following Maven command to build the backend project:

    ```bash
    mvn clean install
    ```

3. After the build is successful, start the Spring Boot application with:

    ```bash
    mvn spring-boot:run
    ```

The backend will start running on **http://localhost:8080/**.

-----------------------------------------
## STEP 6: SET UP THE FRONTEND (React)
-----------------------------------------

1. Navigate to the **frontend** folder:

    ```bash
    cd frontend
    ```

2. Install the necessary packages for the React project:

    ```bash
    npm install
    npm install react-toastify
    npm install react-datepicker
    npm install sweetalert2
    npm install axios
    npm install @stomp/stompjs sockjs-client
    ```

3. Once the packages are installed, start the React development server with:

    ```bash
    npm start
    ```

The frontend will start running on **http://localhost:3000/**.

-----------------------------------------
## STEP 7: STOPPING THE PROJECT
-----------------------------------------

To stop the project, use the following keyboard shortcut in the terminal:

    Ctrl + C  (Windows, Mac, Linux)

This will stop the running processes for both the backend and frontend.

-----------------------------------------
## STEP 8: ENJOY!
-----------------------------------------

After completing these steps, you are now ready to use the application smoothly. We hope you enjoy exploring SoulM! If you encounter any issues, feel free to check the previous steps or refer to the error messages in the terminal.

Happy coding and have fun using SoulM! 🚀

-----------------------------------------
## QUICK SUMMARY
-----------------------------------------

1. **Install Node.js, Maven, and Git.**  
2. **Download the project from GitHub (via Git or ZIP).**  
3. **Set up the MySQL database and import the SQL file.**  
4. **In the backend folder, update the `application.properties` file with your MySQL credentials.**  
5. **In the backend folder, run `mvn clean install` and `mvn spring-boot:run`.**  
6. **In the frontend folder, run `npm install` and the necessary packages, then start the project with `npm start`.**  
7. **Stop the project with `Ctrl + C`.**  
8. **Enjoy!!**
