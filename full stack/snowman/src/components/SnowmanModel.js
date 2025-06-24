import React from 'react';
import { useGLTF } from '@react-three/drei';

const SnowmanModel = (props) => {
  const { scene } = useGLTF('/models/snow_man.glb');
  return <primitive object={scene} {...props} />;
};

export default SnowmanModel; 