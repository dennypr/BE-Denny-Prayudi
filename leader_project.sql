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
-- Table structure for table `leader_project`
--

CREATE TABLE `leader_project` (
  `id` int(11) NOT NULL,
  `project_name` varchar(50) NOT NULL,
  `project_location` varchar(40) NOT NULL,
  `project_tools` varchar(40) NOT NULL,
  `project_year` varchar(10) NOT NULL,
  `id_employee_leader` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `leader_project`
--

INSERT INTO `leader_project` (`id`, `project_name`, `project_location`, `project_tools`, `project_year`, `id_employee_leader`) VALUES
(1, 'Build KMS Q', 'Telkomsel', 'Tibo BW', '2022', 10),
(3, 'KMS', 'Jakarta', 'KMS', '2020', 7);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `leader_project`
--
ALTER TABLE `leader_project`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4xwi1vm34nk9y07b58nogf1n5` (`id_employee_leader`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `leader_project`
--
ALTER TABLE `leader_project`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `leader_project`
--
ALTER TABLE `leader_project`
  ADD CONSTRAINT `FK4xwi1vm34nk9y07b58nogf1n5` FOREIGN KEY (`id_employee_leader`) REFERENCES `employee_leader` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
