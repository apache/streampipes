/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { UnitDescription } from '../model/UnitDescription';
import { Injectable } from '@angular/core';

@Injectable()
export class UnitProviderService {
    private units: UnitDescription[] = [
        {
            resource: 'http://qudt.org/vocab/unit#PerSquareGigaElectronVolt',
            label: 'Per Square Giga Electron Volt Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalResistivityUnit',
            label: 'Thermal Resistivity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MegaHertzPerTesla',
            label: 'Mega Hertz per Tesla',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KilocaloriePerSquareCentimeterSecond',
            label: 'Kilocalorie per Square Centimeter Second',
        },
        {
            resource: 'http://www.openphacts.org/units/Nanometer',
            label: 'Nanometer',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ThermEEC',
            label: 'Therm EC',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerTesla',
            label: 'Joule per Tesla',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeAngle',
            label: 'Degree Angle',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BtuPerSquareFootHourDegreeFahrenheit',
            label: 'BTU per Square Foot Hour Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KelvinPerWatt',
            label: 'Kelvin per Watt',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VolumeThermalExpansionUnit',
            label: 'Volume Thermal Expansion Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilocaloriePerMinute',
            label: 'Kilocalorie per Minute',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThrustToMassRatioUnit',
            label: 'Thrust To Mass Ratio Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VolumeUnit',
            label: 'Volume Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NewtonPerMeter',
            label: 'Newton per Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramPerSquareMeter',
            label: 'Kilogram per Square Meter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#BendingMomentOrTorqueUnit',
            label: 'Bending Moment Or Torque Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LengthUnit',
            label: 'Length Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareFootPerHour',
            label: 'Square Foot per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ElectronVoltPerKelvin',
            label: 'Electron Volt per Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HourSquareFoot',
            label: 'Hour Square Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SpaceAndTimeUnit',
            label: 'Space And Time Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#QuarticCoulombMeterPerCubicEnergy',
            label: 'Quartic Coulomb Meter per Cubic Energy',
        },
        {
            resource: 'http://qudt.org/schema/qudt#DecimalPrefixUnit',
            label: 'Decimal Prefix Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassPerAreaUnit',
            label: 'Mass Per Area Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#InternationalUnitPerLiter',
            label: 'International Unit per Liter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VolumePerTimeUnit',
            label: 'Volume Per Time Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareMeter',
            label: 'Square Meter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AbsorbedDoseUnit',
            label: 'Absorbed Dose Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#CapacitanceUnit',
            label: 'Capacitance Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SerumOrPlasmaLevelUnit',
            label: 'Serum Or Plasma Level Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeFahrenheitPerSecond',
            label: 'Degree Fahrenheit per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterPerHour',
            label: 'Meter per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#InchOfMercury',
            label: 'Inch of Mercury',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Decimeter',
            label: 'Decimeter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PhysicalUnit',
            label: 'Physical Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#OunceImperial',
            label: 'Imperial Ounce',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SpecificHeatVolumeUnit',
            label: 'Specific Heat Volume Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootUSSurvey',
            label: 'US Survey Foot',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilometerPerHour',
            label: 'Kilometer per Hour',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BtuPerPoundMoleDegreeFahrenheit',
            label: 'BTU per Pound Mole Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#OpticsUnit',
            label: 'Light Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NewtonMeter',
            label: 'Newton Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Inch',
            label: 'Inch',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareCentimeterSecond',
            label: 'Square Centimeter Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#InchPerSecond',
            label: 'Inch per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GallonUSPerMinute',
            label: 'US Gallon per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilobitsPerSecond',
            label: 'Kilobit per Second',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalInsulanceUnit',
            label: 'Thermal Insulance Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#HumanUnit',
            label: 'Human Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PlaneAngleUnit',
            label: 'Plane Angle Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#NonSIUnit',
            label: 'Non SI Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Point',
            label: 'Point',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreePerMinute',
            label: 'Degree per Minute',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ChemistryUnit',
            label: 'Chemistry Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PascalSecond',
            label: 'Pascal Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerSecondSquareFoot',
            label: 'BTU per Second Square Foot',
        },
        {
            resource: 'http://qudt.org/vocab/unit#YearSidereal',
            label: 'Year Sidereal',
        },
        {
            resource: 'http://qudt.org/schema/qudt#CountingUnit',
            label: 'Counting Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeFahrenheit',
            label: 'Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerSecond',
            label: 'BTU per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareYard',
            label: 'Square Yard',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SecondAngle',
            label: 'Second Angle',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerDegreeRankine',
            label: 'BTU per Degree Rankine',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Foot',
            label: 'Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VolumePerMassUnit',
            label: 'Volume per mass unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicInchPerMinute',
            label: 'Cubic Inch per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeRankinePerMinute',
            label: 'Degree Rankine per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Ampere',
            label: 'Ampere',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GallonImperial',
            label: 'Imperial Gallon',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalExpansionUnit',
            label: 'Thermal Expansion Unit',
        },
        {
            resource: 'http://www.openphacts.org/units/MilligramPerMilliliter',
            label: 'Milligram per Milliliter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Henry',
            label: 'Henry',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MicroFarad',
            label: 'Microfarad',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RadianPerMinute',
            label: 'Radian per second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Diopter',
            label: 'Diopter',
        },
        {
            resource: 'http://www.openphacts.org/units/Milliliter',
            label: 'Milliliter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Byte',
            label: 'Byte',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramKelvin',
            label: 'Kilogram Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Tesla',
            label: 'Tesla',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Dyne',
            label: 'Dyne',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Are',
            label: 'Are',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DryPintUS',
            label: 'US Dry Pint',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicFoot',
            label: 'Cubic Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricFluxUnit',
            label: 'Electric Flux Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NewtonPerCoulomb',
            label: 'Newton per Coulomb',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MagneticFluxDensityUnit',
            label: 'Magnetic Flux Density Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BritishThermalUnitInternationalTable',
            label: 'British Thermal Unit - International Steam Table',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Mach',
            label: 'Mach',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PowerPerElectricChargeUnit',
            label: 'Energy per Electric Charge Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeCelsiusPerHour',
            label: 'Degree Celsius per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeRankine',
            label: 'Degree Rankine',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareMeterKelvinPerWatt',
            label: 'Square Meter Kelvin per Watt',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DryGallonUS',
            label: 'Dry Gallon US',
        },
        {
            resource: 'http://qudt.org/schema/qudt#RadiologyUnit',
            label: 'Radiology Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LogarithmicUnit',
            label: 'Logarithmic Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AtomicNumber',
            label: 'Atomic Number',
        },
        {
            resource:
                'http://qudt.org/schema/qudt#AmountOfSubstanceTemperatureUnit',
            label: 'Amount of substance temperature unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeRankinePerHour',
            label: 'Degree Rankine per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Kilocalorie',
            label: 'Kilocalorie',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AmpereTurnPerInch',
            label: 'Ampere Turn per Inch',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AngleUnit',
            label: 'Angle unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#CommunicationsUnit',
            label: 'Communications Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreePerSecond',
            label: 'Degree per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RegisterTon',
            label: 'Register Ton',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BtuInchPerSquareFootHourDegreeFahrenheit',
            label: 'BTU Inch per Square Foot Hour Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Peck',
            label: 'US Peck',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilocaloriePerSecond',
            label: 'Kilocalorie per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Farad',
            label: 'Farad',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Hectare',
            label: 'Hectare',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerSquareMeter',
            label: 'Joule per Square Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MicroHenry',
            label: 'Microhenry',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SecondTimeSquared',
            label: 'Second Time Squared',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BitsPerSecond',
            label: 'Bits per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Joule',
            label: 'Joule',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LengthTemperatureTimeUnit',
            label: 'Length Temperature Time Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CentimeterPerSecond',
            label: 'Centimeter per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerCubicMeterKelvin',
            label: 'Joule per Cubic Meter Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Teaspoon',
            label: 'Teaspoon',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ElectronVoltPerTesla',
            label: 'Electron Volt per Tesla',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeRankinePerSecond',
            label: 'Degree Rankine per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PlanckVolume',
            label: 'Planck Volume',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HenryPerMeter',
            label: 'Henry per Meter',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#SquareFootHourDegreeFahrenheitPerBtu',
            label: 'Square Foot Hour Degree Fahrenheit per BTU',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AmperePerJoule',
            label: 'Ampere per Joule',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramPerMole',
            label: 'Kilogram per Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MegaElectronVoltFemtometer',
            label: 'Mega Electron Volt Femtometer',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterPerMinute',
            label: 'Meter per Minute',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#SquareFootSecondDegreeFahrenheit',
            label: 'Square Foot Second Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Liter',
            label: 'Liter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PlanckFrequency',
            label: 'Planck Frequency',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MillimeterOfMercuryAbsolute',
            label: 'Millimeter of Mercury - Absolute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Radian',
            label: 'Radian',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KelvinPerHour',
            label: 'Kelvin per Hour',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SolidAngleUnit',
            label: 'Solid Angle Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ResistanceUnit',
            label: 'Resistance Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramMeterSquared',
            label: 'Kilogram Meter Squared',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicMeterPerMole',
            label: 'Cubic Meter per Mole',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PressureOrStressRateUnit',
            label: 'Pressure or stress rate',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerKilogramKelvin',
            label: 'Joule per Kilogram Kelvin',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MagneticFluxUnit',
            label: 'Magnetic Flux Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MagneticFieldStrengthUnit',
            label: 'Magnetic Field Strength Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#LiquidOunceUS',
            label: 'US Liquid Ounce',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricChargeUnit',
            label: 'Electric Charge Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicMillimeter',
            label: 'Cubic Millimeter',
        },
        {
            resource: 'http://www.openphacts.org/units/GramPerMilliliter',
            label: 'Gram per Milliliter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#LiquidQuartUS',
            label: 'US Liquid Quart',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MoleKelvin',
            label: 'Mole Kelvin',
        },
        {
            resource: 'http://www.openphacts.org/units/PicogramPerMilliliter',
            label: 'Picogram per Milliliter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#EnergyPerElectricChargeUnit',
            label: 'Energy per Electric Charge Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#CurrentPerAngleUnit',
            label: 'Current Per Angle Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GallonUS',
            label: 'US Gallon',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PlanckTime',
            label: 'Planck Time',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermodynamicsUnit',
            label: 'Thermodynamics Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AcreFoot',
            label: 'Acre Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricityAndMagnetismUnit',
            label: 'Electricity And Magnetism Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramPerCubicMeter',
            label: 'Kilogram per Cubic Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Grade',
            label: 'Grade',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareInch',
            label: 'Square Inch',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AreaAngleUnit',
            label: 'Area Angle Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#JoulePerKilogramKelvinPerCubicMeter',
            label: 'Joule per Kilogram Kelvin per Cubic Meter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LinearAccelerationUnit',
            label: 'Linear Acceleration Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeCelsiusPerSecond',
            label: 'Degree Celsius per Second',
        },
        {
            resource: 'http://www.openphacts.org/units/GramPerLiter',
            label: 'Gram per Liter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterPerSecond',
            label: 'Meter per Second',
        },
        {
            resource: 'http://www.openphacts.org/units/MicrogramPerMilliliter',
            label: 'Microgram per Milliliter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PrefixUnit',
            label: 'Prefix Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#VoltPerSquareMeter',
            label: 'Volt per Square Meter',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BtuInchPerSquareFootSecondDegreeFahrenheit',
            label: 'BTU Inch per Square Foot Second Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterKilogram',
            label: 'Meter Kilogram',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Coulomb',
            label: 'Coulomb',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KelvinPerSecond',
            label: 'Kelvin per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicMile',
            label: 'Cubic Mile',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareFoot',
            label: 'Square Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#GravitationalAttractionUnit',
            label: 'Gravitational Attraction Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalEnergyLengthUnit',
            label: 'Thermal Energy Length Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MicrobialFormationUnit',
            label: 'Microbial Formation Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#TimeSquaredUnit',
            label: 'Time Squared Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AmpereHour',
            label: 'Ampere Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HertzPerKelvin',
            label: 'Hertz per Kelvin',
        },
        {
            resource: 'http://qudt.org/schema/qudt#DoseEquivalentUnit',
            label: 'Dose Equivalent Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HertzPerVolt',
            label: 'Hertz per Volt',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Cord',
            label: 'Cord',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ErgSecond',
            label: 'Erg Second',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VelocityUnit',
            label: 'Velocity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NauticalMilePerMinute',
            label: 'Nautical Mile per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareMile',
            label: 'Square Mile',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerKilogram',
            label: 'Joule per Kilogram',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KiloHertz',
            label: 'Kilohertz',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SIDerivedUnit',
            label: 'SI Derived Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#JoulePerKilogramKelvinPerPascal',
            label: 'Joule per Kilogram Kelvin per Pascal',
        },
        {
            resource: 'http://qudt.org/vocab/unit#OuncePerGallon',
            label: 'Ounce per Gallon',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootPoundForcePerSquareFoot',
            label: 'Foot Pound per Square Foot',
        },
        {
            resource: 'http://www.openphacts.org/units/Microliter',
            label: 'Microliter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KiloElectronVolt',
            label: 'Kilo Electron Volt',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootPerSecondSquared',
            label: 'Foot per Second Squared',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MagnetomotiveForceUnit',
            label: 'Magnetomotive Force Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PerMeter',
            label: 'Per Meter Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KilocaloriePerSquareCentimeter',
            label: 'Kilocalorie per Square Centimeter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Yard',
            label: 'Yard',
        },
        {
            resource: 'http://qudt.org/schema/qudt#UsedWithSIUnit',
            label: 'Used With SI Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PlanckTemperature',
            label: 'Planck Temperature',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BritishThermalUnitThermochemical',
            label: 'British Thermal Unit - Thermochemical Calorie',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ComputingUnit',
            label: 'Computing Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PerTeslaSecond',
            label: 'Per Tesla Second Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramPerHour',
            label: 'Kilogram per Hour',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalDiffusivityUnit',
            label: 'Thermal Diffusivity Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PowerPerAreaUnit',
            label: 'Power Per Area Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MilliSecond',
            label: 'Millisecond',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ActivityUnit',
            label: 'Activity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CoulombPerMeter',
            label: 'Coulomb per Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicInch',
            label: 'Cubic Inch',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AtomicPhysicsUnit',
            label: 'Atomic Physics Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerPoundDegreeRankine',
            label: 'BTU per Pound Degree Rankine',
        },
        {
            resource: 'http://qudt.org/schema/qudt#TemperaturePerTimeUnit',
            label: 'Temperature per time unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Tablespoon',
            label: 'Tablespoon',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GramDegreeCelsius',
            label: 'Gram Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MegabitsPerSecond',
            label: 'Megabit per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Milligravity',
            label: 'Milligravity',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MicroSecond',
            label: 'Microsecond',
        },
        {
            resource:
                'http://qudt.org/schema/qudt#MassAmountOfSubstanceTemperatureUnit',
            label: 'Mass Amount Of Substance Temperature Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalResistanceUnit',
            label: 'Thermal Resistance Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Pica',
            label: 'Pica',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PoundMoleDegreeFahrenheit',
            label: 'Pound Mole Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MilliTorr',
            label: 'MilliTorr',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#WattPerSquareMeterQuarticKelvin',
            label: 'Watt per Square Meter Quartic Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#LightYear',
            label: 'Light Year',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SecondTime',
            label: 'Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Fermi',
            label: 'Fermi',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KilocaloriePerSquareCentimeterMinute',
            label: 'Kilocalorie per Square Centimeter Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreePerHour',
            label: 'Degree per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerMoleKelvin',
            label: 'Joule per Mole Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Hertz',
            label: 'Hertz',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JouleMeterPerMole',
            label: 'Joule Meter per Mole',
        },
        {
            resource: 'http://qudt.org/schema/qudt#EnergyDensityUnit',
            label: 'Energy Density Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeCelsiusPerMinute',
            label: 'Degree Celsius per Minute',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SpecificHeatPressureUnit',
            label: 'Specific Heat Pressure Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GallonUSPerDay',
            label: 'US Gallon per Day',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MicroInch',
            label: 'Microinch',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AccelerationUnit',
            label: 'Acceleration Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CoulombPerKilogram',
            label: 'Coulomb per Kilogram',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CircularMil',
            label: 'Circular Mil',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RadianPerHour',
            label: 'Radian per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterPerFarad',
            label: 'Meter per Farad',
        },
        {
            resource: 'http://qudt.org/schema/qudt#CurvatureUnit',
            label: 'Curvature Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KelvinPerMinute',
            label: 'Kelvin per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CandelaPerSquareInch',
            label: 'Candela per Square Inch',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Furlong',
            label: 'Furlong',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KiloPascalAbsolute',
            label: 'Kilopascal Absolute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DryQuartUS',
            label: 'US Dry Quart',
        },
        {
            resource: 'http://qudt.org/vocab/unit#InchOfWater',
            label: 'Inch of Water',
        },
        {
            resource:
                'http://qudt.org/schema/qudt#SignalDetectionThresholdUnit',
            label: 'Signal Detection Threshold Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricDipoleMomentUnit',
            label: 'Electric Dipole Moment Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#DecibelReferredToOneMilliwatt',
            label: 'Decibel Referred to 1mw',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RadianPerSecond',
            label: 'Radian per Second',
        },
        {
            resource: 'http://qudt.org/schema/qudt#RF-PowerUnit',
            label: 'RF-Power Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerPound',
            label: 'BTU per Pound',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Day',
            label: 'Day',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicMeterPerSecond',
            label: 'Cubic Meter per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeFahrenheitPerHour',
            label: 'Degree Fahrenheit per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareCentimeterMinute',
            label: 'Square Centimeter Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HourSidereal',
            label: 'Hour Sidereal',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AreaUnit',
            label: 'Area Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareMeterSteradian',
            label: 'Square Meter Steradian',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LinearVelocityUnit',
            label: 'Linear Velocity Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#EventUnit',
            label: 'Event Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareFootPerBtuInch',
            label: 'Square Foot per BTU Inch',
        },
        {
            resource: 'http://qudt.org/schema/qudt#DynamicViscosityUnit',
            label: 'Dynamic Viscosity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicFootPerSecond',
            label: 'Cubic Foot per Second',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ForcePerLengthUnit',
            label: 'Force Per Length Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PoundMole',
            label: 'Pound Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CandelaPerSquareMeter',
            label: 'Candela per Square Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Kilometer',
            label: 'Kilometer ',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Knot',
            label: 'Knot',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerHourSquareFoot',
            label: 'BTU per Hour Square Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MolarHeatCapacityUnit',
            label: 'Molar Heat Capacity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PintImperial',
            label: 'Imperial Pint',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CoulombPerCubicMeter',
            label: 'Coulomb per Cubic Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootPoundForcePerSquareMeter',
            label: 'Foot Pound Force per Square Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HeartBeatsPerMinute',
            label: 'Heart Beat per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#WattPerMeterKelvin',
            label: 'Watt per Meter Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Pascal',
            label: 'Pascal',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CalorieThermochemical',
            label: 'Thermochemical Calorie',
        },
        {
            resource: 'http://qudt.org/vocab/unit#TonOfRefrigeration',
            label: 'Ton of Refrigeration',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Chain',
            label: 'Chain',
        },
        {
            resource: 'http://qudt.org/vocab/unit#InchPerSecondSquared',
            label: 'Inch per Second Squared',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Becquerel',
            label: 'Becquerel',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PerSecond',
            label: 'Inverse Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeFahrenheitHour',
            label: 'Degree Fahrenheit Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JouleSecondPerMole',
            label: 'Joule Second per Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilocaloriePerMole',
            label: 'Kilocalorie per Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#LiquidPintUS',
            label: 'US Liquid Pint',
        },
        {
            resource: 'http://qudt.org/vocab/unit#OerstedCentimeter',
            label: 'Oersted Centimeter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MillionDollarsPerFlight',
            label: 'Million US Dollars per Flight',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareFootDegreeFahrenheit',
            label: 'Square Foot Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#HeatCapacityAndEntropyUnit',
            label: 'Heat Capacity And Entropy Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Mole',
            label: 'Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PerTeslaMeter',
            label: 'Per Tesla Meter Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MegaHertzPerKelvin',
            label: 'Mega Hertz per Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Kilogram',
            label: 'Kilogram',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassPerLengthUnit',
            label: 'Mass Per Length Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AtomicChargeUnit',
            label: 'Atomic Charge Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Femtometer',
            label: 'Femtometer',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BoardFoot',
            label: 'Board Foot',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NauticalMilePerHour',
            label: 'Nautical Mile per Hour',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassPerVolumeUnit',
            label: 'Mass Per Volume Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KelvinPerTesla',
            label: 'Kelvin per Tesla',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BreathPerMinute',
            label: 'Breath per Minute',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MomentumUnit',
            label: 'Momentum Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PermittivityUnit',
            label: 'Permittivity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreePerSecondSquared',
            label: 'Degree per Second Squared',
        },
        {
            resource: 'http://qudt.org/schema/qudt#FinancialUnit',
            label: 'Financial Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SpecificEnergyUnit',
            label: 'Specific Energy Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#NotUsedWithSIUnit',
            label: 'Not Used With SI Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#EnergyAndWorkUnit',
            label: 'Energy And Work Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#InformationEntropyUnit',
            label: 'Information Entropy Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Stere',
            label: 'Stere',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Lux',
            label: 'Lux',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Gram',
            label: 'Gram',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Kilowatt',
            label: 'Kilowatt',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MilePerMinute',
            label: 'Mile per Minute',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SIBaseUnit',
            label: 'SI Base Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootPerSecond',
            label: 'Foot per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Tex',
            label: 'Tex',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BtuPerSquareFootSecondDegreeFahrenheit',
            label: 'BTU per Square Foot Second Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AmperePerSquareMeter',
            label: 'Ampere per Square Meter',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#SquareFootHourDegreeFahrenheit',
            label: 'Square Foot Hour Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#EnergyPerAreaUnit',
            label: 'Energy Per Area Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterKelvin',
            label: 'Meter Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerSquareFoot',
            label: 'BTU per Square Foot',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CoulombMeter',
            label: 'Coulomb Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilocaloriePerGram',
            label: 'Kilocalorie per Gram',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AbsorbedDoseRateUnit',
            label: 'Absorbed Dose Rate Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#CentimeterSecondDegreeCelsius',
            label: 'Centimeter Second Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DecibelCarrier',
            label: 'Decibel Carrier Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PoundDegreeFahrenheit',
            label: 'Pound Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MilePerHour',
            label: 'Mile per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FaradPerMeter',
            label: 'Farad per Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PerCubicMeter',
            label: 'Per Cubic Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Parsec',
            label: 'Parsec',
        },
        {
            resource: 'http://qudt.org/vocab/unit#WattSquareMeter',
            label: 'Watt Square Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Gray',
            label: 'Gray',
        },
        {
            resource: 'http://qudt.org/vocab/unit#HertzPerTesla',
            label: 'Hertz per Tesla',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ForcePerElectricChargeUnit',
            label: 'Force Per Electric Charge Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerPoundDegreeFahrenheit',
            label: 'BTU per Pound Degree Fahrenheit',
        },
        {
            resource: 'http://www.openphacts.org/units/NanogramPerMilliliter',
            label: 'Nanogram per Milliliter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicYard',
            label: 'Cubic Yard',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeCentigrade',
            label: 'Degree Centigrade',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareCentimeter',
            label: 'Square Centimeter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Shake',
            label: 'Shake',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SecondSquareFoot',
            label: 'Second Square Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SIUnit',
            label: 'SI Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Lumen',
            label: 'Lumen',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MegaElectronVolt',
            label: 'Mega Electron Volt',
        },
        {
            resource: 'http://qudt.org/schema/qudt#TimeAreaUnit',
            label: 'Time Area Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GrayPerSecond',
            label: 'Gray per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Bushel',
            label: 'Bushel',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MinuteTime',
            label: 'Minute Time',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicCentimeter',
            label: 'Cubic Centimeter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LinearEnergyTransferUnit',
            label: 'Linear Energy Transfer Unit',
        },
        {
            resource:
                'http://qudt.org/schema/qudt#EnergyAndWorkPerMassAmountOfSubstance',
            label: 'Energy and work per mass amount of substance',
        },
        {
            resource: 'http://qudt.org/schema/qudt#CatalyticActivityUnit',
            label: 'Catalytic Activity Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VideoFrameRateUnit',
            label: 'Video Frame Rate Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NumberPerYear',
            label: 'Number per Year',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MinuteAngle',
            label: 'Minute Angle',
        },
        {
            resource: 'http://qudt.org/schema/qudt#TurbidityUnit',
            label: 'Turbidity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeCelsiusCentimeter',
            label: 'Degree Celsius Centimeter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PicoFarad',
            label: 'Picofarad',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KiloPascal',
            label: 'Kilopascal',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RevolutionPerHour',
            label: 'Revolution per Hour',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricFieldStrengthUnit',
            label: 'Electric Field Strength Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#TemperatureUnit',
            label: 'Temperature Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicFootPerMinute',
            label: 'Cubic Foot per Minute',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassAmountOfSubstanceUnit',
            label: 'Mass Amount Of Substance Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MechanicsUnit',
            label: 'Mechanics Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AmountOfSubstanceUnit',
            label: 'Amount Of Substance Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Meter',
            label: 'Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramSecondSquared',
            label: 'Kilogram Second Squared',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RevolutionPerMinute',
            label: 'Revolution per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Barrel',
            label: 'Barrel',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AreaThermalExpansionUnit',
            label: 'Area Thermal Expansion Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#WattSquareMeterPerSteradian',
            label: 'Watt Square Meter per Steradian',
        },
        {
            resource:
                'http://qudt.org/schema/qudt#InverseAmountOfSubstanceUnit',
            label: 'Inverse Amount Of Substance Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#TeslaMeter',
            label: 'Tesla Meter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricCurrentDensityUnit',
            label: 'Electric Current Density Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassTemperatureUnit',
            label: 'Mass Temperature Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramPerSecond',
            label: 'Kilogram per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Dalton2',
            label: 'Atomic mass unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PermeabilityUnit',
            label: 'Permeability Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Watt',
            label: 'Watt',
        },
        {
            resource: 'http://qudt.org/schema/qudt#unitFor',
            label: 'unit for',
        },
        {
            resource: 'http://www.openphacts.org/units/SquareAngstrom',
            label: 'Square Ångström',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RevolutionPerSecond',
            label: 'Revolution per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Fathom',
            label: 'Fathom',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FramePerSecond',
            label: 'Frame per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Weber',
            label: 'Weber',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JouleSecond',
            label: 'Joule Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerSquareTesla',
            label: 'Joule per Square Tesla',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Barn',
            label: 'Barn',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicMeter',
            label: 'Cubic Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MegaHertz',
            label: 'Megahertz',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PressureOrStressUnit',
            label: 'Pressure Or Stress Unit',
        },
        {
            resource: 'http://www.openphacts.org/units/MilligramPerDeciliter',
            label: 'Milligram per Deciliter',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KilocaloriePerMoleDegreeCelsius',
            label: 'Kilocalorie per Mole Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterKelvinPerWatt',
            label: 'Meter Kelvin per Watt',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MilLength',
            label: 'Mil Length',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ExposureUnit',
            label: 'Exposure Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GigaElectronVolt',
            label: 'Giga Electron Volt',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ForceUnit',
            label: 'Force Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#VoltPerMeter',
            label: 'Volt per Meter',
        },
        {
            resource: 'http://www.openphacts.org/units/LiterPerSecond',
            label: 'liter per second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PlanckLength',
            label: 'Planck Length',
        },
        {
            resource: 'http://qudt.org/schema/qudt#SpecificHeatCapacityUnit',
            label: 'Specific Heat Capacity Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassUnit',
            label: 'Mass Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ConductanceUnit',
            label: 'Conductance Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricChargeDensityUnit',
            label: 'Electric Charge Density Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#FrequencyUnit',
            label: 'Frequency Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AngularAccelerationUnit',
            label: 'Angular Acceleration Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Newton',
            label: 'Newton',
        },
        {
            resource: 'http://qudt.org/schema/qudt#DataRateUnit',
            label: 'Data Rate Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Kelvin',
            label: 'Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareMeterKelvin',
            label: 'Square Meter Kelvin',
        },
        {
            resource: 'http://qudt.org/schema/qudt#KinematicViscosityUnit',
            label: 'Kinematic Viscosity Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KilocaloriePerGramDegreeCelsius',
            label: 'Calorie per Gram Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramMeterPerSecond',
            label: 'Kilogram Meter Per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MilliHenry',
            label: 'Millihenry',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Siemens',
            label: 'Siemens',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AngularMassUnit',
            label: 'Angular Mass Unit',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#MegaElectronVoltPerCentimeter',
            label: 'Mega Electron Volt per Centimeter',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KiloElectronVoltPerMicrometer',
            label: 'Kilo Electron Volt per Micrometer',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AngularVelocityUnit',
            label: 'Angular Velocity Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Centimeter',
            label: 'Centimeter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MileUSStatute',
            label: 'Mile US Statute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#GigaHertz',
            label: 'Gigahertz',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NanoFarad',
            label: 'Nanofarad',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Decibel',
            label: 'Decibel',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeFahrenheitHourPerBtu',
            label: 'Degree Fahrenheit Hour per BTU',
        },
        {
            resource: 'http://qudt.org/schema/qudt#HeartRateUnit',
            label: 'Heart Rate Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PlanckArea',
            label: 'Planck Area',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AtomicMassUnit',
            label: 'Atomic Mass Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#PowerUnit',
            label: 'Power Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Sievert',
            label: 'Sievert',
        },
        {
            resource:
                'http://qudt.org/schema/qudt#CoefficientOfHeatTransferUnit',
            label: 'Coefficient Of Heat Transfer Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Unitless',
            label: 'Unitless',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AmperePerMeter',
            label: 'Ampere per Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareMeterPerSecond',
            label: 'Square Meter per Second',
        },
        {
            resource: 'http://www.openphacts.org/units/LiterPerMinute',
            label: 'liter per minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Clo',
            label: 'Clo',
        },
        {
            resource: 'http://qudt.org/vocab/unit#RadianPerSecondSquared',
            label: 'Radian per Second Squared',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AreaTemperatureUnit',
            label: 'Area Temperature Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Angstrom',
            label: 'Angstrom',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeCelsius',
            label: 'Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MoleDegreeCelsius',
            label: 'Mole Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KnotPerSecond',
            label: 'Knot per Second',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MassPerTimeUnit',
            label: 'Mass Per Time Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#YearTropical',
            label: 'Year Tropical',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootPerHour',
            label: 'Foot per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerDegreeFahrenheit',
            label: 'BTU per Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MileInternational',
            label: 'Mile - International Standard',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ArcSecond',
            label: 'Arc Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ThermUS',
            label: 'Therm US',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CoulombSquareMeter',
            label: 'Coulomb Square Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MillionDollarsPerYear',
            label: 'Million US Dollars per Year',
        },
        {
            resource: 'http://qudt.org/schema/qudt#RespiratoryRateUnit',
            label: 'Respiratory Rate Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilometerPerSecond',
            label: 'Kilometer per Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerHour',
            label: 'BTU per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MeterPerSecondSquared',
            label: 'Meter per Second Squared',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectrochemistryUnit',
            label: 'Electrochemistry Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Katal',
            label: 'Katal',
        },
        {
            resource: 'http://qudt.org/vocab/unit#SquareCoulombMeterPerJoule',
            label: 'Square Coulomb Meter per Joule',
        },
        {
            resource: 'http://qudt.org/vocab/unit#LiquidCupUS',
            label: 'US Liquid Cup',
        },
        {
            resource: 'http://qudt.org/schema/qudt#HeatFlowRateUnit',
            label: 'Heat Flow Rate Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerCubicMeter',
            label: 'Joule per Cubic Meter',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalEnergyUnit',
            label: 'Thermal Energy Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Volt',
            label: 'Volt',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DegreeFahrenheitPerMinute',
            label: 'Degree Fahrenheit per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CentimeterPerSecondSquared',
            label: 'Centimeter per Second Squared',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#KilocaloriePerCentimeterSecondDegreeCelsius',
            label: 'Calorie per Centimeter Second Degree Celsius',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Torr',
            label: 'Torr',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AstronomicalUnit',
            label: 'Astronomical Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#KilogramPerMeter',
            label: 'Kilogram per Meter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CalorieNutritional',
            label: 'Nutritional Calorie',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Year365Day',
            label: 'Year (365 Day)',
        },
        {
            resource: 'http://qudt.org/schema/qudt#BiomedicalUnit',
            label: 'Biomedical Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicMeterPerHour',
            label: 'Cubic Meter per Hour',
        },
        {
            resource: 'http://qudt.org/vocab/unit#WattPerSquareMeterKelvin',
            label: 'Watt per Square Meter Kelvin',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#CubicCoulombMeterPerSquareJoule',
            label: 'Cubic Coulomb Meter per Square Joule',
        },
        {
            resource: 'http://qudt.org/schema/qudt#TimeUnit',
            label: 'Time Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#VolumePerTimeSquaredUnit',
            label: 'Volume per Time Squared Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#DaySidereal',
            label: 'Day Sidereal',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LengthTemperatureUnit',
            label: 'Length Temperature Unit',
        },
        {
            resource: 'http://www.openphacts.org/units/MicroliterPerMinute',
            label: 'microliter per minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerMole',
            label: 'Joule per Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Millimeter',
            label: 'Millimeter',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MicroTorr',
            label: 'MicroTorr',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuInch',
            label: 'BTU Inch',
        },
        {
            resource:
                'http://qudt.org/vocab/unit#BtuFootPerSquareFootHourDegreeFahrenheit',
            label: 'BTU Foot per Square Foot Hour Degree Fahrenheit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#AmperePerDegree',
            label: 'Ampere per Degree',
        },
        {
            resource: 'http://qudt.org/schema/qudt#MolarEnergyUnit',
            label: 'Molar Energy Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#ArcMinute',
            label: 'Arc Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#JoulePerKelvin',
            label: 'Joule per Kelvin',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MillimeterOfMercury',
            label: 'Millimeter of Mercury',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Acre',
            label: 'Acre',
        },
        {
            resource: 'http://qudt.org/vocab/unit#CubicYardPerMinute',
            label: 'Cubic Yard per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#TeslaSecond',
            label: 'Tesla Second',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PoundDegreeRankine',
            label: 'Pound Degree Rankine',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NauticalMile',
            label: 'Nautical Mile',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MillionUSDollars',
            label: 'Million US Dollars',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Candela',
            label: 'Candela',
        },
        {
            resource: 'http://qudt.org/vocab/unit#MinuteSidereal',
            label: 'Minute Sidereal',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Micrometer',
            label: 'Micrometer',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ThermalConductivityUnit',
            label: 'Thermal Conductivity Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ElectricCurrentUnit',
            label: 'Electric Current Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Ohm',
            label: 'Ohm',
        },
        {
            resource: 'http://qudt.org/schema/qudt#AreaTimeTemperatureUnit',
            label: 'Area Time Temperature Unit',
        },
        {
            resource: 'http://qudt.org/schema/qudt#ConcentrationUnit',
            label: 'Concentration Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#PerMeterKelvin',
            label: 'Per Meter Kelvin Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuFoot',
            label: 'BTU Foot',
        },
        {
            resource: 'http://qudt.org/schema/qudt#InductanceUnit',
            label: 'Inductance Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#NewtonPerKilogram',
            label: 'Newton per Kilogram',
        },
        {
            resource: 'http://qudt.org/vocab/unit#FootPerMinute',
            label: 'Foot per Minute',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Rod',
            label: 'Rod',
        },
        {
            resource: 'http://qudt.org/schema/qudt#LinearThermalExpansionUnit',
            label: 'Linear Thermal Expansion Unit',
        },
        {
            resource: 'http://qudt.org/vocab/unit#BtuPerPoundMole',
            label: 'BTU per Pound Mole',
        },
        {
            resource: 'http://qudt.org/vocab/unit#Hour',
            label: 'Hour',
        },
    ];

    getUnits(): UnitDescription[] {
        return this.units;
    }
}
