/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Declare the console as an ambient value so that TypeScript doesn't complain.
declare var console: any;

// Import the application components and services.
import { ILogger } from "./default-log.service";


// I log values to the ambient console object.
export class ConsoleLogService implements ILogger {

    public assert( ...args: any[] ) : void {

        ( console && console.assert ) && console.assert( ...args );

    }


    public error( ...args: any[] ) : void {

        ( console && console.error ) && console.error( ...args );

    }


    public group( ...args: any[] ) : void {

        ( console && console.group ) && console.group( ...args );

    }


    public groupEnd( ...args: any[] ) : void {

        ( console && console.groupEnd ) && console.groupEnd( ...args );

    }


    public info( ...args: any[] ) : void {

        ( console && console.info ) && console.info( ...args );

    }


    public log( ...args: any[] ) : void {

        ( console && console.log ) && console.log( ...args );

    }


    public warn( ...args: any[] ) : void {

        ( console && console.warn ) && console.warn( ...args );

    }

}