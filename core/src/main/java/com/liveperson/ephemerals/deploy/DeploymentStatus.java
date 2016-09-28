package com.liveperson.ephemerals.deploy;

/**
 * Created by waseemh on 9/16/16.
 */
public enum DeploymentStatus {

        /**
         * The app or group is being deployed. If there are multiple apps or
         * app instances, at least one of them is still being deployed.
         */
         DEPLOYING,

         /**
         * All apps have been successfully deployed.
         */
        DEPLOYED,

        /**
         * The app or group is known to the system, but is not currently deployed.
         */
        UNDEPLOYED,

        /**
         * In the case of multiple apps, some have successfully deployed, while
         * others have not. This state does not apply for individual app instances.
         */
        PARTIAL,

        /**
         * All apps have failed deployment.
         */
        FAILED,

        /**
         * A system error occurred trying to determine deployment status.
         */
        ERROR,

        /**
         * The app or group deployment is not known to the system.
         */
        UKNOWN;

}
