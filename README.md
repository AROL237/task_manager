
### TASK MANGER APP

## INSTALLION PROCESS

1. Pull the the code to your local PC
2. Make sure to have docker install to your local manchine
3. Or if you want to deploy the app a GCE in a docker environment,
    
    -    Make sur to have docker install it that GCE
    -    Copy nginx.env,the root docker-compose.yml file, prod.env to your GCE.

        ```
        NB: This 'prod.env' is not means to be share on the public repo or git repo just like i dit. In this case it was done so because it is a test example.
        ```

        
    - Then simply run   `docker compose up` command to to start the app.
    -this will pull image and `signing/tm_ui:0.0.1` and `signig/tm_api:0.0.1` from my docker hub repo.
    - for a complete CICD pipeline, i will write a `jenkins server` . to setup the this process to be triggered on a push request.

    - make sure to opp the port :8080  on your server,
     so that the app can be accessible from your browser.
     `your-server-ip:8080/`


4.


#### Enjoy app  😁







