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
