-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 21, 2022 at 08:03 AM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `belajar`
--

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `id` int(11) NOT NULL,
  `project_name` varchar(50) NOT NULL,
  `location` varchar(40) NOT NULL,
  `tools` varchar(40) NOT NULL,
  `year` varchar(10) NOT NULL,
  `id_employee` int(11) DEFAULT NULL,
  `id_employee_leader` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`id`, `project_name`, `location`, `tools`, `year`, `id_employee`, `id_employee_leader`) VALUES
(8, 'Build KMS s', 'Telkomsel', 'Tibo BW', '2022', 9, NULL),
(11, 'Build KMS s', 'Telkomsel', 'Tibo BW', '2022', 13, NULL),
(12, 'Build KMS s', 'Telkomsel', 'Tibo BW', '2022', 14, NULL),
(13, 'Build KMS s', 'Telkomsel', 'Tibo BW', '2022', 15, NULL),
(14, 'Build KMS s', 'Telkomsel', 'Tibo BW', '2022', 16, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKjvqa8e0ft7x70guhu2upp4qts` (`id_employee`),
  ADD KEY `FK3g4s8kyyfskel3kfwsnr6k3kn` (`id_employee_leader`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `project`
--
ALTER TABLE `project`
  ADD CONSTRAINT `FK3g4s8kyyfskel3kfwsnr6k3kn` FOREIGN KEY (`id_employee_leader`) REFERENCES `employee_leader` (`id`),
  ADD CONSTRAINT `FKjvqa8e0ft7x70guhu2upp4qts` FOREIGN KEY (`id_employee`) REFERENCES `employee` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

@echo off
title Aktivasi Microsoft Office 2019 Tanpa Software - BagiTekno&cls&echo ============================================================================&echo #Project: Aktivasi Microsoft Office 2019 FREE tanpa software&echo ============================================================================&echo.&echo #Supported products:&echo - Microsoft Office Standard 2019&echo - Microsoft Office Professional Plus 2019&echo.&echo.&(if exist "%ProgramFiles%\Microsoft Office\Office16\ospp.vbs" cd /d "%ProgramFiles%\Microsoft Office\Office16")&(if exist "%ProgramFiles(x86)%\Microsoft Office\Office16\ospp.vbs" cd /d "%ProgramFiles(x86)%\Microsoft Office\Office16")&(for /f %%x in ('dir /b ..\root\Licenses16\ProPlus2019VL*.xrm-ms') do cscript ospp.vbs /inslic:"..\root\Licenses16\%%x" >nul)&(for /f %%x in ('dir /b ..\root\Licenses16\ProPlus2019VL*.xrm-ms') do cscript ospp.vbs /inslic:"..\root\Licenses16\%%x" >nul)&echo.&echo ============================================================================&echo Mengaktivasi Office 2019 anda, silahkan tunggu...&cscript //nologo slmgr.vbs /ckms >nul&cscript //nologo ospp.vbs /setprt:1688 >nul&cscript //nologo ospp.vbs /unpkey:6MWKP >nul&set i=1&cscript //nologo ospp.vbs /inpkey:NMMKJ-6RK4F-KMJVX-8D9MJ-6MWKP >nul||goto notsupported
:server
if %i% GTR 10 goto busy
if %i% EQU 1 set KMS=s8.uk.to
if %i% EQU 2 set KMS=s9.uk.to
if %i% EQU 3 set KMS=kms7.MSGuides.com
if %i% EQU 4 set KMS=kms8.MSGuides.com
if %i% EQU 5 set KMS=kms9.MSGuides.com
if %i% GTR 5 goto ato
cscript //nologo ospp.vbs /sethst:%KMS% >nul
:ato
echo ============================================================================&echo.&echo.&cscript //nologo ospp.vbs /act | find /i "successful" && (echo.&echo ============================================================================&echo.&echo.&echo #Dukung kami agar cara ini bisa terus digunakan!&echo #Donasi bisa via saweria.co/bagitekno atau donate.msguides.com&echo #Terima kasih atas dukungan anda!&echo.&echo ============================================================================& if errorlevel 2 exit) || (echo Sepertinya butuh waktu lebih, silahkan tunggu... & echo. & echo. & set /a i+=1 & goto server)&explorer "https://saweria.co/bagitekno"&explorer "https://youtu.be/LGJ9GMn17Zk"&goto halt
:notsupported
echo.&echo ============================================================================&echo Maaf, Office 2019 anda tidak support.&echo.&goto halt
:busy
echo =====================================================================================&echo.&echo Server sedang sibuk, sedang mencoba lagi, silahkan tunggu...&echo.&set /a i=1&goto server
:halt
cd %~dp0&del %0 >nul&pause >nul
