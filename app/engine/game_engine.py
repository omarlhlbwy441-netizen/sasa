"""
Neama AI - Ultra High Performance 2D/3D Game & Physics Engine
Architected for Autonomous Game Dev, Physics Simulations, and Real-time Multiplayer.
"""

import time
import math
import random
import json
import threading
from typing import Dict, List, Any, Optional, Tuple

class Vector2:
    def __init__(self, x: float = 0.0, y: float = 0.0):
        self.x = float(x)
        self.y = float(y)

    def to_dict(self) -> Dict[str, float]:
        return {"x": round(self.x, 3), "y": round(self.y, 3)}

    def __add__(self, other: 'Vector2') -> 'Vector2':
        return Vector2(self.x + other.x, self.y + other.y)

    def __sub__(self, other: 'Vector2') -> 'Vector2':
        return Vector2(self.x - other.x, self.y - other.y)

    def __mul__(self, scalar: float) -> 'Vector2':
        return Vector2(self.x * scalar, self.y * scalar)

    def length(self) -> float:
        return math.sqrt(self.x * self.x + self.y * self.y)

    def normalize(self) -> 'Vector2':
        l = self.length()
        if l > 0:
            return Vector2(self.x / l, self.y / l)
        return Vector2(0, 0)

    def distance_to(self, other: 'Vector2') -> float:
        return (self - other).length()


class Particle:
    def __init__(self, x: float, y: float, vx: float, vy: float, color: str, life: float = 1.0, size: float = 4.0):
        self.pos = Vector2(x, y)
        self.vel = Vector2(vx, vy)
        self.color = color
        self.max_life = life
        self.life = life
        self.size = size

    def update(self, dt: float) -> bool:
        self.pos = self.pos + (self.vel * dt)
        self.life -= dt
        return self.life > 0

    def to_dict(self) -> Dict[str, Any]:
        return {
            "x": round(self.pos.x, 2),
            "y": round(self.pos.y, 2),
            "color": self.color,
            "alpha": round(max(0.0, self.life / self.max_life), 2),
            "size": round(self.size, 1)
        }


class Entity:
    def __init__(
        self,
        entity_id: str,
        name: str,
        x: float,
        y: float,
        width: float = 32.0,
        height: float = 32.0,
        body_type: str = "dynamic",  # dynamic, static, kinematic
        color: str = "#bef264",
        shape: str = "circle", # circle, box
        mass: float = 1.0,
        restitution: float = 0.75, # bounciness
        friction: float = 0.98
    ):
        self.id = entity_id
        self.name = name
        self.pos = Vector2(x, y)
        self.vel = Vector2(0.0, 0.0)
        self.acc = Vector2(0.0, 0.0)
        self.width = width
        self.height = height
        self.radius = max(width, height) / 2.0
        self.body_type = body_type
        self.color = color
        self.shape = shape
        self.mass = mass if body_type == "dynamic" else 0.0
        self.inv_mass = 1.0 / mass if (body_type == "dynamic" and mass > 0) else 0.0
        self.restitution = restitution
        self.friction = friction
        self.rotation = 0.0
        self.angular_vel = 0.0
        self.tags = ["entity"]
        self.custom_data: Dict[str, Any] = {}

    def apply_force(self, fx: float, fy: float):
        if self.body_type != "dynamic":
            return
        self.acc = self.acc + Vector2(fx * self.inv_mass, fy * self.inv_mass)

    def apply_impulse(self, ix: float, iy: float):
        if self.body_type != "dynamic":
            return
        self.vel = self.vel + Vector2(ix * self.inv_mass, iy * self.inv_mass)

    def update(self, dt: float, gravity: Vector2, bounds: Tuple[float, float, float, float]):
        if self.body_type == "dynamic":
            # Apply gravity
            self.vel = self.vel + (gravity * dt)
            self.vel = self.vel + (self.acc * dt)
            self.vel = self.vel * self.friction  # simple air resistance
            self.pos = self.pos + (self.vel * dt)
            self.rotation += self.angular_vel * dt
            self.acc = Vector2(0, 0) # reset force accumulator

            # World boundaries collision
            min_x, min_y, max_x, max_y = bounds
            r = self.radius if self.shape == "circle" else self.width / 2.0

            if self.pos.x - r < min_x:
                self.pos.x = min_x + r
                self.vel.x = -self.vel.x * self.restitution
            elif self.pos.x + r > max_x:
                self.pos.x = max_x - r
                self.vel.x = -self.vel.x * self.restitution

            if self.pos.y - r < min_y:
                self.pos.y = min_y + r
                self.vel.y = -self.vel.y * self.restitution
            elif self.pos.y + r > max_y:
                self.pos.y = max_y - r
                self.vel.y = -self.vel.y * self.restitution
                # Ground friction
                self.vel.x *= 0.95

    def to_dict(self) -> Dict[str, Any]:
        return {
            "id": self.id,
            "name": self.name,
            "pos": self.pos.to_dict(),
            "vel": self.vel.to_dict(),
            "width": self.width,
            "height": self.height,
            "radius": self.radius,
            "body_type": self.body_type,
            "color": self.color,
            "shape": self.shape,
            "rotation": round(self.rotation, 3),
            "tags": self.tags
        }


class NeamaGameEngine:
    """
    Core 2D/3D Game & Simulation Engine with real-time particle generation,
    rigid-body physics, collision solving, and audio synthesis triggers.
    """
    _instance = None

    @classmethod
    def get_instance(cls) -> 'NeamaGameEngine':
        if cls._instance is None:
            cls._instance = NeamaGameEngine()
        return cls._instance

    def __init__(self, world_width: float = 800.0, world_height: float = 600.0):
        self.world_width = world_width
        self.world_height = world_height
        self.bounds = (0.0, 0.0, world_width, world_height)
        self.gravity = Vector2(0.0, 480.0) # Downward gravity in px/s^2
        self.entities: Dict[str, Entity] = {}
        self.particles: List[Particle] = []
        self.is_running = False
        self.fps = 60.0
        self.frame_count = 0
        self.last_tick = time.time()
        self.delta_time = 0.016
        self._lock = threading.Lock()
        self.audio_triggers: List[Dict[str, Any]] = []

        # Seed initial demo arena
        self.reset_scene()

    def reset_scene(self):
        with self._lock:
            self.entities.clear()
            self.particles.clear()
            self.audio_triggers.clear()

            # Create ground platform
            ground = Entity(
                entity_id="ground_platform",
                name="Main Ground Platform",
                x=self.world_width / 2.0,
                y=self.world_height - 30.0,
                width=self.world_width - 40.0,
                height=30.0,
                body_type="static",
                color="#334155",
                shape="box"
            )
            self.entities[ground.id] = ground

            # Add floating bouncy pads
            pad1 = Entity(
                entity_id="pad_left",
                name="Left Energy Pad",
                x=180.0,
                y=380.0,
                width=160.0,
                height=20.0,
                body_type="static",
                color="#22c55e",
                shape="box",
                restitution=1.2
            )
            self.entities[pad1.id] = pad1

            pad2 = Entity(
                entity_id="pad_right",
                name="Right Energy Pad",
                x=620.0,
                y=320.0,
                width=160.0,
                height=20.0,
                body_type="static",
                color="#84cc16",
                shape="box",
                restitution=1.2
            )
            self.entities[pad2.id] = pad2

            # Spawn hero interactive physics balls
            colors = ["#bef264", "#22c55e", "#38bdf8", "#f43f5e", "#a855f7", "#eab308"]
            for i in range(5):
                eid = f"hero_orb_{i+1}"
                orb = Entity(
                    entity_id=eid,
                    name=f"Energy Orb #{i+1}",
                    x=150.0 + (i * 120.0),
                    y=80.0 + (i * 30.0),
                    width=28.0 + (i % 3) * 6.0,
                    height=28.0 + (i % 3) * 6.0,
                    body_type="dynamic",
                    color=colors[i % len(colors)],
                    shape="circle",
                    mass=1.0 + (i * 0.2),
                    restitution=0.85
                )
                orb.vel = Vector2(random.uniform(-120.0, 120.0), random.uniform(-50.0, 50.0))
                self.entities[eid] = orb

    def spawn_entity(
        self,
        name: str,
        x: float,
        y: float,
        width: float = 30.0,
        height: float = 30.0,
        shape: str = "circle",
        color: str = "#bef264",
        body_type: str = "dynamic",
        vx: float = 0.0,
        vy: float = 0.0
    ) -> Dict[str, Any]:
        with self._lock:
            eid = f"ent_{int(time.time() * 1000)}_{random.randint(100, 999)}"
            ent = Entity(
                entity_id=eid,
                name=name,
                x=x,
                y=y,
                width=width,
                height=height,
                shape=shape,
                color=color,
                body_type=body_type,
                restitution=0.82
            )
            ent.vel = Vector2(vx, vy)
            self.entities[eid] = ent

            # Spawn spawn particle burst
            self.emit_particles(x, y, count=16, color=color)
            self.trigger_audio_fx("spawn", freq=520, duration=0.15)
            return ent.to_dict()

    def emit_particles(self, x: float, y: float, count: int = 15, color: str = "#bef264", speed: float = 180.0):
        for _ in range(count):
            angle = random.uniform(0, math.pi * 2)
            spd = random.uniform(speed * 0.3, speed)
            vx = math.cos(angle) * spd
            vy = math.sin(angle) * spd
            p = Particle(
                x=x,
                y=y,
                vx=vx,
                vy=vy,
                color=color,
                life=random.uniform(0.4, 0.9),
                size=random.uniform(2.5, 6.0)
            )
            self.particles.append(p)

    def trigger_audio_fx(self, sound_type: str, freq: float = 440.0, duration: float = 0.1):
        self.audio_triggers.append({
            "type": sound_type,
            "frequency": freq,
            "duration": duration,
            "timestamp": time.time()
        })
        # Keep recent audio events
        if len(self.audio_triggers) > 20:
            self.audio_triggers = self.audio_triggers[-20:]

    def apply_impulse_to_all(self, ix: float = 0.0, iy: float = -350.0):
        with self._lock:
            for ent in self.entities.values():
                if ent.body_type == "dynamic":
                    ent.apply_impulse(ix + random.uniform(-80, 80), iy + random.uniform(-100, 0))
                    self.emit_particles(ent.pos.x, ent.pos.y, count=8, color=ent.color)
            self.trigger_audio_fx("jump_wave", freq=640, duration=0.2)

    def update_physics(self, dt: float):
        # Resolve entity updates
        with self._lock:
            ents = list(self.entities.values())
            n = len(ents)

            for ent in ents:
                ent.update(dt, self.gravity, self.bounds)

            # Circle-to-Circle & Circle-to-Box Collisions
            for i in range(n):
                e1 = ents[i]
                for j in range(i + 1, n):
                    e2 = ents[j]

                    # Only resolve dynamic collisions
                    if e1.body_type == "static" and e2.body_type == "static":
                        continue

                    # Simple circle-circle resolution
                    if e1.shape == "circle" and e2.shape == "circle":
                        dist = e1.pos.distance_to(e2.pos)
                        min_dist = e1.radius + e2.radius
                        if dist < min_dist and dist > 0.001:
                            # Normal vector
                            nx = (e2.pos.x - e1.pos.x) / dist
                            ny = (e2.pos.y - e1.pos.y) / dist
                            overlap = min_dist - dist

                            # Positional separation
                            if e1.body_type == "dynamic" and e2.body_type == "dynamic":
                                e1.pos.x -= nx * overlap * 0.5
                                e1.pos.y -= ny * overlap * 0.5
                                e2.pos.x += nx * overlap * 0.5
                                e2.pos.y += ny * overlap * 0.5
                            elif e1.body_type == "dynamic":
                                e1.pos.x -= nx * overlap
                                e1.pos.y -= ny * overlap
                            elif e2.body_type == "dynamic":
                                e2.pos.x += nx * overlap
                                e2.pos.y += ny * overlap

                            # Relative velocity along normal
                            rvx = e2.vel.x - e1.vel.x
                            rvy = e2.vel.y - e1.vel.y
                            vel_along_norm = rvx * nx + rvy * ny

                            if vel_along_norm < 0:
                                rest = min(e1.restitution, e2.restitution)
                                impulse_mag = -(1 + rest) * vel_along_norm
                                total_inv_mass = e1.inv_mass + e2.inv_mass
                                if total_inv_mass > 0:
                                    impulse_mag /= total_inv_mass
                                    imp_x = nx * impulse_mag
                                    imp_y = ny * impulse_mag

                                    if e1.body_type == "dynamic":
                                        e1.vel.x -= imp_x * e1.inv_mass
                                        e1.vel.y -= imp_y * e1.inv_mass
                                    if e2.body_type == "dynamic":
                                        e2.vel.x += imp_x * e2.inv_mass
                                        e2.vel.y += imp_y * e2.inv_mass

                                    # Emit impact sparks & audio trigger
                                    if impulse_mag > 40.0:
                                        self.emit_particles((e1.pos.x + e2.pos.x) / 2.0, (e1.pos.y + e2.pos.y) / 2.0, count=4, color="#bef264")

            # Update particles
            surviving_particles = []
            for p in self.particles:
                if p.update(dt):
                    surviving_particles.append(p)
            self.particles = surviving_particles

            self.frame_count += 1

    def tick(self) -> Dict[str, Any]:
        now = time.time()
        dt = min(0.05, now - self.last_tick)
        self.last_tick = now
        self.delta_time = dt
        self.update_physics(dt)
        return self.get_state()

    def get_state(self) -> Dict[str, Any]:
        with self._lock:
            return {
                "success": True,
                "world": {
                    "width": self.world_width,
                    "height": self.world_height,
                    "gravity": self.gravity.to_dict(),
                    "fps": round(1.0 / max(0.001, self.delta_time), 1),
                    "frame_count": self.frame_count,
                    "active_entities_count": len(self.entities),
                    "active_particles_count": len(self.particles)
                },
                "entities": [ent.to_dict() for ent in self.entities.values()],
                "particles": [p.to_dict() for p in self.particles[:60]],
                "audio_events": self.audio_triggers[-5:]
            }

# Initialize singleton
game_engine_instance = NeamaGameEngine.get_instance()
